package ghostcache.api      // ← match the proto package

import com.google.protobuf.ByteString
import grpc.ghostcache.*                   // if you still need the session types elsewhere
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Base64
import java.util.logging.Logger

class DekCacheServiceImpl(
    private val redis: RedisCommands<String, String>
) : DekCacheServiceGrpcKt.DekCacheServiceCoroutineImplBase() {

    private val log = Logger.getLogger("dekcacheservice")

    /* ------------------- PutDek ------------------- */
    override suspend fun putDek(
        request: PutDekRequest
    ): PutDekReply = withContext(Dispatchers.IO) {

        val ttl  = if (request.ttlSec > 0) request.ttlSec else 86_400  // default 24 h
        val blob = Base64.getEncoder().encodeToString(request.wrappedKey.toByteArray())

        // key, seconds, value
        redis.setex(request.keyId, ttl.toLong(), blob)

        log.info("PutDek key=${request.keyId} len=${blob.length} ttl=$ttl")
        PutDekReply.newBuilder().setOk(true).build()
    }

    /* ------------------- GetDek ------------------- */
    override suspend fun getDek(
        request: GetDekRequest
    ): GetDekReply = withContext(Dispatchers.IO) {

        val stored   = redis.get(request.keyId)
        val ttlLeft  = redis.ttl(request.keyId)        // ‑2=no key, ‑1=no expiry
        val ttlSafe  = if (ttlLeft > 0) ttlLeft.toInt() else 0

        log.info(
            "GetDek key=${request.keyId} len=${stored?.length ?: -1} ttl=$ttlLeft raw='${stored?.take(20)}'"
        )

        if (!stored.isNullOrEmpty()) {
            GetDekReply.newBuilder()
                .setFound(true)
                .setWrappedKey(ByteString.copyFrom(Base64.getDecoder().decode(stored)))
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
