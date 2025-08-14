package ghostcache.api      // ← match your package

import com.google.protobuf.ByteString
import grpc.ghostcache.*
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.logging.Logger

class DekCacheServiceImpl(
    private val redis: RedisCommands<String, ByteArray>   // String, ByteArray
) : DekCacheServiceGrpcKt.DekCacheServiceCoroutineImplBase() {

    private val log = Logger.getLogger("dekcacheservice")

    /* ------------------- PutDek ------------------- */
    override suspend fun putDek(
        request: PutDekRequest
    ): PutDekReply = withContext(Dispatchers.IO) {
        val ttl = if (request.ttlSec > 0) request.ttlSec else 86_400 // 24h
        val bytes = request.wrappedKey.toByteArray()
        redis.setex(request.keyId, ttl.toLong(), bytes)
        PutDekReply.newBuilder().setOk(true).build()
    }

    /* ------------------- GetDek ------------------- */
    override suspend fun getDek(
        request: GetDekRequest
    ): GetDekReply = withContext(Dispatchers.IO) {
        val stored  = redis.get(request.keyId)
        val ttlLeft = redis.ttl(request.keyId)           // -2=no key, -1=no expiry
        val ttlSafe = if (ttlLeft > 0) ttlLeft.toInt() else 0

        if (stored != null) {
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
    override suspend fun deleteDek(
        request: DeleteDekRequest
    ): DeleteDekReply = withContext(Dispatchers.IO) {
        val removed = redis.del(request.keyId) > 0
        DeleteDekReply.newBuilder().setOk(removed).build()
    }
}
