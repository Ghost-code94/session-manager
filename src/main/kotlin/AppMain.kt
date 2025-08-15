package ghostcache.api

import io.grpc.netty.NettyServerBuilder
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel   // <-- add
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

    val redisUri = (System.getenv("REDIS_URL")?.takeIf { it.isNotBlank() }
        ?: error("REDIS_URL not set or blank"))

    val jwtSecret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: error("JWT_SECRET not set or blank")

    // ---- Lettuce with small pools ----
    val resources = DefaultClientResources.builder()
        .ioThreadPoolSize(2)
        .computationThreadPoolSize(2)
        .build()

    val client = RedisClient.create(resources, redisUri)
    client.setDefaultTimeout(Duration.ofSeconds(2))

    val bytesCodec: RedisCodec<String, ByteArray> =
        RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)

    val connBytes = client.connect(bytesCodec)
    val redisBytesAsync = connBytes.async()

    // ---- Netty event loops (1 vCPU) ----
    val boss   = NioEventLoopGroup(1)
    val worker = NioEventLoopGroup(1)
    val appExecutor = Executors.newFixedThreadPool(4)

    val server = NettyServerBuilder.forPort(grpcPort)
        .intercept(JwtAuthInterceptor(jwtSecret))

        // You supplied boss/worker → you MUST also supply channelType:
        .bossEventLoopGroup(boss)
        .workerEventLoopGroup(worker)
        .channelType(NioServerSocketChannel::class.java) // <-- REQUIRED

        .executor(appExecutor)
        .permitKeepAliveWithoutCalls(true)
        .keepAliveTime(60, TimeUnit.SECONDS)
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .maxConnectionIdle(10, TimeUnit.MINUTES)
        .maxConcurrentCallsPerConnection(32)
        .flowControlWindow(256 * 1024)
        .maxInboundMessageSize(1 * 1024 * 1024)
        .build()
        .start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        connBytes.close()
        client.shutdown()
        resources.shutdown()
        appExecutor.shutdown()
        boss.shutdownGracefully()
        worker.shutdownGracefully()
    })

    server.awaitTermination()
}
