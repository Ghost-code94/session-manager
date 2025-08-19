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

    // Choose the best available transport with explicit typing to help Kotlin inference
    val transport: Triple<EventLoopGroup, EventLoopGroup, Class<out ServerChannel>> =
        when {
            System.getenv("USE_IO_URING") == "1" && IOUring.isAvailable() -> {
                println("▶ Using io_uring transport")
                Triple(
                    IOUringEventLoopGroup(1),
                    IOUringEventLoopGroup(),
                    IOUringServerSocketChannel::class.java
                )
            }
            Epoll.isAvailable() -> {
                println("▶ Using epoll transport")
                Triple(
                    EpollEventLoopGroup(1),
                    EpollEventLoopGroup(),
                    EpollServerSocketChannel::class.java
                )
            }
            else -> {
                println("▶ Using NIO transport (fallback)")
                Triple(
                    NioEventLoopGroup(1),
                    NioEventLoopGroup(),
                    NioServerSocketChannel::class.java
                )
            }
        }

    val boss: EventLoopGroup = transport.first
    val worker: EventLoopGroup = transport.second
    val channelClazz: Class<out ServerChannel> = transport.third

    // (Optional) print unavailability causes to aid debugging
    if (!Epoll.isAvailable()) {
        val cause = io.netty.channel.epoll.Epoll.unavailabilityCause()
        if (cause != null) println("epoll unavailable: ${cause.message}")
    }
    if (System.getenv("USE_IO_URING") == "1" && !IOUring.isAvailable()) {
        val cause = IOUring.unavailabilityCause()
        if (cause != null) println("io_uring unavailable: ${cause.message}")
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
        .channelType(channelClazz)
        .executor(appExecutor)
        .permitKeepAliveWithoutCalls(true)
        .keepAliveTime(60, TimeUnit.SECONDS)
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .maxConnectionIdle(10, TimeUnit.MINUTES)
        .maxConcurrentCallsPerConnection(48)
        .flowControlWindow(256 * 1024)
        .maxInboundMessageSize(1 * 1024 * 1024)
        .addService(SessionServiceAsyncImpl(rMut))
        .addService(DekCacheServiceAsyncImpl(rMut))
        .build()
        .start()

    println("✅ gRPC(Session) on :$grpcPort | Redis @$redisUri")

    Runtime.getRuntime().addShutdownHook(Thread {
        try {
            server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        } finally {
            try { cR.close() } catch (_: Throwable) {}
            try { cW.close() } catch (_: Throwable) {}
            try { client.shutdown() } catch (_: Throwable) {}
            try { resources.shutdown() } catch (_: Throwable) {}
            try { appExecutor.shutdown() } catch (_: Throwable) {}
            try { boss.shutdownGracefully() } catch (_: Throwable) {}
            try { worker.shutdownGracefully() } catch (_: Throwable) {}
        }
    })

    server.awaitTermination()
}
