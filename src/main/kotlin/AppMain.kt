// src/main/kotlin/ghostcache/api/AppMain.kt
package ghostcache.api

import io.grpc.netty.NettyServerBuilder
import io.netty.channel.EventLoopGroup
import io.netty.channel.ServerChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.incubator.channel.uring.IOUring
import io.netty.incubator.channel.uring.IOUringEventLoopGroup
import io.netty.incubator.channel.uring.IOUringServerSocketChannel
import grpc.ghostcache.auth.JwtAuthInterceptor
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.codec.StringCodec
import io.lettuce.core.resource.DefaultClientResources
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import ghostcache.api.SessionServiceAsyncImpl
import ghostcache.api.DekCacheServiceAsyncImpl

fun main() {
    val grpcPort  = System.getenv("GRPC_PORT")?.toInt() ?: 50051
    val redisUri  = System.getenv("REDIS_URL")?.takeIf { it.isNotBlank() }
        ?: error("REDIS_URL not set or blank")
    val jwtSecret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: error("JWT_SECRET not set or blank")

    val (boss, worker, channelClazz) = when {
        // opt-in io_uring with a flag; comment out if you want auto without flag
        System.getenv("USE_IO_URING") == "1" && IOUring.isAvailable() -> {
            println("▶ Using io_uring transport")
            Triple(IOUringEventLoopGroup(1), IOUringEventLoopGroup(), IOUringServerSocketChannel::class.java)
        }
        Epoll.isAvailable() -> {
            println("▶ Using epoll transport")
            Triple(EpollEventLoopGroup(1), EpollEventLoopGroup(), EpollServerSocketChannel::class.java)
        }
        else -> {
            println("▶ Using NIO transport (fallback)")
            Triple(NioEventLoopGroup(1), NioEventLoopGroup(), NioServerSocketChannel::class.java)
        }
    }

    val resources = DefaultClientResources.builder()
        .ioThreadPoolSize(2)
        .computationThreadPoolSize(2)
        .build()

    val client = RedisClient.create(resources, redisUri)
    client.setDefaultTimeout(Duration.ofSeconds(2))

    // ✅ String keys + ByteArray values
    val codec: RedisCodec<String, ByteArray> =
        RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)

    val cR  = client.connect(codec)  // read socket (optional)
    val cW  = client.connect(codec)  // write socket
    val rGet = cR.async()            // RedisAsyncCommands<String, ByteArray>
    val rMut = cW.async()            // RedisAsyncCommands<String, ByteArray>

    val appExecutor = Executors.newFixedThreadPool(4)

    val server = NettyServerBuilder.forPort(grpcPort)
        .intercept(JwtAuthInterceptor(jwtSecret))
        .bossEventLoopGroup(boss)
        .workerEventLoopGroup(worker)
        .channelType(NioServerSocketChannel::class.java)
        .executor(appExecutor)
        .permitKeepAliveWithoutCalls(true)
        .keepAliveTime(60, TimeUnit.SECONDS)
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .maxConnectionIdle(10, TimeUnit.MINUTES)
        .maxConcurrentCallsPerConnection(48)
        .flowControlWindow(256 * 1024)
        .maxInboundMessageSize(1 * 1024 * 1024)

        // ✅ types now match the service constructors
        .addService(SessionServiceAsyncImpl(rMut))
        .addService(DekCacheServiceAsyncImpl(rMut))

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
