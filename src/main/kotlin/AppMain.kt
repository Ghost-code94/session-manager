package ghostcache.api

import io.grpc.netty.NettyServerBuilder
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.StringCodec 
import java.util.concurrent.TimeUnit
import grpc.ghostcache.auth.JwtAuthInterceptor

fun main() {
    val grpcPort = System.getenv("GRPC_PORT")?.toInt() ?: 50051
    val redisUri = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
    val jwtSecret = System.getenv("JWT_SECRET")
        ?: error("JWT_SECRET environment variable not set")

    // ── Redis ────────────────────────────────────────────────
    val client     = RedisClient.create(redisUri)
    val connection = client.connect(StringCodec.UTF8)          // ✅ UTF-8 strings
    val redis      = connection.sync()

    // ── gRPC server ──────────────────────────────────────────
    val server = NettyServerBuilder.forPort(grpcPort)
        .intercept(JwtAuthInterceptor(jwtSecret))      // ← your existing auth
        .addService(SessionServiceImpl(redis))      // new session API
        .addService(DekCacheServiceImpl(redis))
        // .permitKeepAliveWithoutCalls(true)
        .permitKeepAliveWithoutCalls(false)
        .keepAliveTime(30, TimeUnit.SECONDS)
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .maxInboundMessageSize(4 * 1024 * 1024)
        .maxConnectionIdle(2, TimeUnit.MINUTES)
        .maxConnectionAge(5, TimeUnit.MINUTES)
        .maxConnectionAgeGrace(30, TimeUnit.SECONDS)

        .build()
        .start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        connection.close(); client.shutdown()
    })

    server.awaitTermination()
}
