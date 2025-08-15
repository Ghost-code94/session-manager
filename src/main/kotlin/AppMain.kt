package ghostcache.api

import io.grpc.netty.NettyServerBuilder
import io.netty.channel.nio.NioEventLoopGroup
import io.lettuce.core.RedisClient
import io.lettuce.core.resource.DefaultClientResources
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.codec.StringCodec
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import grpc.ghostcache.auth.JwtAuthInterceptor

fun main() {
    val grpcPort = System.getenv("GRPC_PORT")?.toInt() ?: 50051
    val redisUri = System.getenv("REDIS_URL")?.takeIf { it.isNotBlank() }
        ?: error("REDIS_URL not set or blank")
    val jwtSecret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: error("JWT_SECRET not set or blank")

    // ---- Lettuce: small thread pools (this process already runs Netty for gRPC) ----
    val resources = DefaultClientResources.builder()
        .ioThreadPoolSize(2)          // I/O threads for Redis
        .computationThreadPoolSize(2) // timers, reconnect, etc.
        .build()

    val client = RedisClient.create(resources, redisUri)
    client.setDefaultTimeout(Duration.ofSeconds(2))

    // String keys + raw bytes values
    val bytesCodec: RedisCodec<String, ByteArray> =
        RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)

    val connBytes = client.connect(bytesCodec)
    val redisBytesAsync = connBytes.async()

    // ---- gRPC/Netty: minimal threads on a 1 vCPU machine ----
    val boss   = NioEventLoopGroup(1)
    val worker = NioEventLoopGroup(1)

    // Small CPU-bound executor for app logic (coroutines hop here when needed)
    val appExecutor = Executors.newFixedThreadPool(4)

    val server = NettyServerBuilder.forPort(grpcPort)
        .intercept(JwtAuthInterceptor(jwtSecret))
        .bossEventLoopGroup(boss)
        .workerEventLoopGroup(worker)
        .executor(appExecutor)

        // --- Connection health & churn ---
        .permitKeepAliveWithoutCalls(true)   // allow pings on idle conns
        .keepAliveTime(60, TimeUnit.SECONDS) // reduce ping churn vs 30s
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .maxConnectionIdle(10, TimeUnit.MINUTES) // close long-idle conns
        // avoid forced reconnect churn: remove maxConnectionAge/Grace

        // --- Back-pressure & memory guards ---
        .maxConcurrentCallsPerConnection(32) // cap H2 streams/conn (sessions/DEK are small)
        .flowControlWindow(256 * 1024)       // 256 KiB per-stream window (lower mem than 1MiB)
        .maxInboundMessageSize(1 * 1024 * 1024) // 1 MiB (sessions/DEK should be << this)

        // keep yours:
        // .maxInboundMessageSize(4 * 1024 * 1024) // only if you truly need 4MiB
        .build()
        .start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        connBytes.close(); client.shutdown(); resources.shutdown()
        appExecutor.shutdown()
        boss.shutdownGracefully(); worker.shutdownGracefully()
    })

    server.awaitTermination()
}
