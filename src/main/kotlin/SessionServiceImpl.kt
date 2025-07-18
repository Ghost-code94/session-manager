package ghostcache.api        // whatever package you use

import com.google.protobuf.ByteString   // ✅ add
import grpc.ghostcache.*                // already present
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.logging.Logger


class SessionServiceImpl(
    private val redis: RedisCommands<String, String>   // String,String
) : SessionServiceGrpcKt.SessionServiceCoroutineImplBase() {
    /* --------------- PutSession ------------------- */
    private val log = Logger.getLogger("sessionservice")

    override suspend fun putSession(
        request: PutSessionRequest
    ): PutSessionReply = withContext(Dispatchers.IO) {

        val ttl  = if (request.ttlSec > 0) request.ttlSec else 3_600
        val blob = Base64.getEncoder().encodeToString(request.payload.toByteArray())
        redis.setex(request.sessionId, ttl.toLong(), blob)

        log.info("PutSession sid=${request.sessionId} len=${blob.length}")

        PutSessionReply.newBuilder().setOk(true).build()
    }

    override suspend fun getSession(
        request: GetSessionRequest
    ): GetSessionReply = withContext(Dispatchers.IO) {

        val stored = redis.get(request.sessionId)
        log.info(
            "GetSession sid=${request.sessionId} len=${stored?.length ?: -1} raw='${stored?.take(20)}'"
        )

        if (!stored.isNullOrEmpty()) {
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
