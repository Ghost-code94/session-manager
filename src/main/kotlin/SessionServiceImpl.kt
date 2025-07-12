package ghostcache.api

import grpc.ghostcache.SessionServiceGrpcKt.SessionServiceCoroutineImplBase
import grpc.ghostcache.*
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SessionServiceImpl(
    private val redis: RedisCommands<String, String>
) : SessionServiceCoroutineImplBase() {

    private fun key(id: String) = "session:$id".toByteArray()

    override suspend fun createSession(
        request: CreateSessionRequest
    ): CreateSessionReply = withContext(Dispatchers.IO) {
        val id = UUID.randomUUID().toString()
        val ttl = if (request.ttlSec > 0) request.ttlSec else 3600
        redis.setex(key(id), ttl.toLong(), request.userId.toByteArray())
        CreateSessionReply.newBuilder().setSessionId(id).build()
    }

    override suspend fun putSession(
        request: PutSessionRequest
    ): PutSessionReply {
        val ttl  = if (request.ttlSec > 0) request.ttlSec else 3_600
        val key  = request.sessionId                        // already String
        val blob = Base64.getEncoder().encodeToString(      // keep payload binary
            request.payload.toByteArray()
        )

        redis.setex(key, ttl.toLong(), blob)               // key & value = String
        return PutSessionReply.newBuilder().setOk(true).build()
    }

    override suspend fun getSession(
        request: GetSessionRequest
    ): GetSessionReply {
        val stored = redis.get(request.sessionId)
        return if (stored != null) {
            GetSessionReply.newBuilder()
                .setValid(true)
                .setPayload(
                    ByteString.copyFrom(Base64.getDecoder().decode(stored))
                )
                .build()
        } else {
            GetSessionReply.newBuilder().setValid(false).build()
        }
    }

    override suspend fun deleteSession(
        request: DeleteSessionRequest
    ): DeleteSessionReply {
        val removed = redis.del(request.sessionId) > 0
        return DeleteSessionReply.newBuilder().setOk(removed).build()
    }
}
