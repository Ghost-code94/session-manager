package ghostcache.api

import io.grpc.netty.NettyServerBuilder
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.ByteArrayCodec
import java.util.concurrent.TimeUnit

fun main() {
    val grpcPort = System.getenv("GRPC_PORT")?.toInt() ?: 50051
    val redisUri = System.getenv("REDIS_URL") ?: "redis://localhost:6379"
    val jwtSecret = System.getenv("JWT_SECRET")
        ?: error("JWT_SECRET environment variable not set")

    // ── Redis ────────────────────────────────────────────────
    val client      = RedisClient.create(redisUri)
    val connection  = client.connect(ByteArrayCodec())
    val commands    = connection.sync()

    // ── gRPC server ──────────────────────────────────────────
    val server = NettyServerBuilder.forPort(grpcPort)
        .intercept(JwtAuthInterceptor(jwtSecret))      // ← your existing auth
        .addService(SessionServiceImpl(commands))      // new session API
        .permitKeepAliveWithoutCalls(true)
        .build()
        .start()

    println("✅ gRPC server on port $grpcPort (Redis @ $redisUri)")

    Runtime.getRuntime().addShutdownHook(Thread {
        server.shutdown().awaitTermination(5, TimeUnit.SECONDS)
        connection.close(); client.shutdown()
    })

    server.awaitTermination()
}
