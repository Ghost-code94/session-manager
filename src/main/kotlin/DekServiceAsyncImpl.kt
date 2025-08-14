package ghostcache.api

import com.google.protobuf.ByteString
import grpc.ghostcache.*
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.future.await

class DekCacheServiceAsyncImpl(
    private val redis: RedisAsyncCommands<String, ByteArray>
) : DekCacheServiceGrpcKt.DekCacheServiceCoroutineImplBase() {

    /* ------------------- PutDek ------------------- */
    override suspend fun putDek(request: PutDekRequest): PutDekReply {
        val ttl   = if (request.ttlSec > 0) request.ttlSec else 86_400 // 24h
        val bytes = request.wrappedKey.toByteArray()
        redis.setex(request.keyId, ttl.toLong(), bytes).await()
        return PutDekReply.newBuilder().setOk(true).build()
    }

    /* ------------------- GetDek ------------------- */
    override suspend fun getDek(request: GetDekRequest): GetDekReply {
        val stored  = redis.get(request.keyId).await()
        val ttlLeft = redis.ttl(request.keyId).await()     // -2=no key, -1=no expiry
        val ttlSafe = if (ttlLeft > 0) ttlLeft.toInt() else 0

        return if (stored != null) {
            GetDekReply.newBuilder()
                .setFound(true)
                .setWrappedKey(ByteString.copyFrom(stored))
                .setTtlSecLeft(ttlSafe)
                .build()
        } else {
            GetDekReply.newBuilder().setFound(false).build()
        }
    }

    /* ---------------- DeleteDek ------------------- */
    override suspend fun deleteDek(request: DeleteDekRequest): DeleteDekReply {
        val removed = redis.unlink(request.keyId).await() > 0  // non-blocking delete
        return DeleteDekReply.newBuilder().setOk(removed).build()
    }
}
