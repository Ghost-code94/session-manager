package ghostcache.api

import grpc.ghostcache.SessionServiceGrpcKt.SessionServiceCoroutineImplBase
import grpc.ghostcache.*
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SessionServiceImpl(
    private val redis: RedisCommands<ByteArray, ByteArray>
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

    override suspend fun getSession(
        request: GetSessionRequest
    ): GetSessionReply = withContext(Dispatchers.IO) {
        val data = redis.get(key(request.sessionId))
        if (data != null) {
            GetSessionReply.newBuilder()
                .setValid(true)
                .setUserId(String(data))
                .build()
        } else {
            GetSessionReply.newBuilder().setValid(false).build()
        }
    }

    override suspend fun deleteSession(
        request: DeleteSessionRequest
    ): DeleteSessionReply = withContext(Dispatchers.IO) {
        val removed = redis.del(key(request.sessionId)) > 0
        DeleteSessionReply.newBuilder().setOk(removed).build()
    }
}
