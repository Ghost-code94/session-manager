// src/main/kotlin/ghostcache/api/AppMain.kt
package ghostcache.api

import io.grpc.netty.NettyServerBuilder
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import grpc.ghostcache.auth.JwtAuthInterceptor
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.resource.DefaultClientResources
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// import your service impls
import ghostcache.api.SessionServiceAsyncImpl
import ghostcache.api.DekCacheServiceAsyncImpl   // if you serve DEK here too

fun main() {
    val grpcPort  = System.getenv("GRPC_PORT")?.toInt() ?: 50051
    val redisUri  = System.getenv("REDIS_URL")?.takeIf { it.isNotBlank() }
        ?: error("REDIS_URL not set or blank")
    val jwtSecret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: error("JWT_SECRET not set or blank")

    // Lettuce: tiny pools (this process already runs Netty for gRPC)
    val resources = DefaultClientResources.builder()
        .ioThreadPoolSize(2)
        .computationThreadPoolSize(2)
        .build()

    val client = RedisClient.create(resources, redisUri)
    client.setDefaultTimeout(Duration.ofSeconds(2))

    // ByteArray<->ByteArray (your SessionService uses ByteArray values)
    val cR = client.connect(ByteArrayCodec.INSTANCE) // optional read socket
    val cW = client.connect(ByteArrayCodec.INSTANCE) // write socket
    val rGet = cR.async()
    val rMut = cW.async()

    // Minimal Netty groups; specify channelType to satisfy the assertion
    val boss   = NioEventLoopGroup(1)
    val worker = NioEventLoopGroup(1)
    val appExecutor = Executors.newFixedThreadPool(4)

    val server = NettyServerBuilder.forPort(grpcPort)
        .intercept(JwtAuthInterceptor(jwtSecret))
        .bossEventLoopGroup(boss)
        .workerEventLoopGroup(worker)
        .channelType(NioServerSocketChannel::class.java)   // <-- important when custom groups set
        .executor(appExecutor)

        // connection health
        .permitKeepAliveWithoutCalls(true)
        .keepAliveTime(60, TimeUnit.SECONDS)
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .maxConnectionIdle(10, TimeUnit.MINUTES)

        // back-pressure & memory guards
        .maxConcurrentCallsPerConnection(48)
        .flowControlWindow(256 * 1024)
        .maxInboundMessageSize(1 * 1024 * 1024)

        // ✅ REGISTER THE SERVICE(S)
        .addService(SessionServiceAsyncImpl(rMut))          // SessionService
        .addService(DekCacheServiceAsyncImpl(rMut))      // (optional) DEK service

        .build()
        .start()

    println("✅ gRPC(Session) on :$grpcPort | Redis @$redisUri")

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        cR.close(); cW.close()
        client.shutdown(); resources.shutdown()
        appExecutor.shutdown()
        boss.shutdownGracefully(); worker.shutdownGracefully()
    })

    server.awaitTermination()
}
