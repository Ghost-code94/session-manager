package ghostcache.api

import com.google.protobuf.ByteString
import grpc.ghostcache.*
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.future.await

class SessionServiceAsyncImpl(
    private val redis: RedisAsyncCommands<String, ByteArray>
) : SessionServiceGrpcKt.SessionServiceCoroutineImplBase() {

    override suspend fun putSession(
        request: PutSessionRequest
    ): PutSessionReply {
        val ttl = if (request.ttlSec > 0) request.ttlSec else 3_600
        val bytes = request.payload.toByteArray()
        redis.setex(request.sessionId, ttl.toLong(), bytes).await()
        return PutSessionReply.newBuilder().setOk(true).build()
    }

    override suspend fun getSession(
        request: GetSessionRequest
    ): GetSessionReply {
        val stored = redis.get(request.sessionId).await()
        return if (stored != null) {
            GetSessionReply.newBuilder()
                .setValid(true)
                .setPayload(ByteString.copyFrom(stored))
                .build()
        } else {
            GetSessionReply.newBuilder().setValid(false).build()
        }
    }

    override suspend fun deleteSession(
        request: DeleteSessionRequest
    ): DeleteSessionReply {
        val removed = redis.del(request.sessionId).await() > 0
        return DeleteSessionReply.newBuilder().setOk(removed).build()
    }
}
