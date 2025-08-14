package ghostcache.api

import io.grpc.netty.NettyServerBuilder
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.codec.StringCodec
import java.time.Duration
import java.util.concurrent.TimeUnit
import grpc.ghostcache.auth.JwtAuthInterceptor

fun main() {
    val grpcPort = System.getenv("GRPC_PORT")?.toInt() ?: 50051
    val redisUri = System.getenv("REDIS_URL")
    val jwtSecret = System.getenv("JWT_SECRET")?.takeIf { it.isNotBlank() }
        ?: error("JWT_SECRET environment variable not set or blank")

    val client = RedisClient.create(redisUri)
    client.setDefaultTimeout(Duration.ofSeconds(2))

    val bytesCodec: RedisCodec<String, ByteArray> =
        RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE)
    val connBytes = client.connect(bytesCodec)
    val redisBytes = connBytes.sync() // RedisAsyncCommands<String, ByteArray>

    val connStr = client.connect(StringCodec.UTF8)
    val redisStr = connStr.async()     // RedisAsyncCommands<String, String>

    val server = NettyServerBuilder.forPort(grpcPort)
        .intercept(JwtAuthInterceptor(jwtSecret))
        .addService(SessionServiceAsyncImpl(redisBytes)) // async version
        .addService(DekCacheServiceImpl(redisBytes))  // if/when you convert
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
        connBytes.close(); connStr.close(); client.shutdown()
    })

    server.awaitTermination()
}
