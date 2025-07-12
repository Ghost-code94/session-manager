package ghostcache.api        // whatever package you use

import com.google.protobuf.ByteString   // ✅ add
import grpc.ghostcache.*                // already present
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class SessionServiceImpl(
    private val redis: RedisCommands<String, String>   // String,String
) : SessionServiceGrpcKt.SessionServiceCoroutineImplBase() {

    /* --------------- CreateSession ---------------- */
    override suspend fun createSession(
        request: CreateSessionRequest
    ): CreateSessionReply = withContext(Dispatchers.IO) {
        val id  = UUID.randomUUID().toString()
        val ttl = if (request.ttlSec > 0) request.ttlSec else 3_600
        val blob = Base64.getEncoder().encodeToString(request.payload.toByteArray())

        redis.setex(id, ttl.toLong(), blob)          // key + value are String
        CreateSessionReply.newBuilder().setSessionId(id).build()
    }

    /* --------------- PutSession ------------------- */
    override suspend fun putSession(
        request: PutSessionRequest
    ): PutSessionReply = withContext(Dispatchers.IO) {
        val ttl  = if (request.ttlSec > 0) request.ttlSec else 3_600
        val blob = Base64.getEncoder().encodeToString(request.payload.toByteArray())
        redis.setex(request.sessionId, ttl.toLong(), blob)
        PutSessionReply.newBuilder().setOk(true).build()
    }

    /* --------------- GetSession ------------------- */
    override suspend fun getSession(
        request: GetSessionRequest
    ): GetSessionReply = withContext(Dispatchers.IO) {
        val stored = redis.get(request.sessionId)
        if (stored != null) {
            GetSessionReply.newBuilder()
                .setValid(true)
                .setPayload(ByteString.copyFrom(Base64.getDecoder().decode(stored)))
                .build()
        } else {
            GetSessionReply.newBuilder().setValid(false).build()
        }
    }

    /* --------------- DeleteSession ---------------- */
    override suspend fun deleteSession(
        request: DeleteSessionRequest
    ): DeleteSessionReply = withContext(Dispatchers.IO) {
        val removed = redis.del(request.sessionId) > 0
        DeleteSessionReply.newBuilder().setOk(removed).build()
    }
}
