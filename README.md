# ghostcache.api (gRPC + Redis session/dek cache)

ghostcache.api is a small, fast gRPC server that stores and retrieves binary payloads in Redis, protected by a JWT Bearer token on every call.

It’s designed to be:
Low-latency (Netty transport auto-select: `io_uring` → `epoll` → NIO)
Simple to deploy (fat JAR in a minimal Alpine image + custom JRE via `jlink`)
Safe-by-default (rejects requests without a valid JWT)

---

## What’s in this package

### Runtime components
- **gRPC server** via `io.grpc.netty.NettyServerBuilder`
- **JWT auth** via a gRPC `ServerInterceptor` (`grpc.ghostcache.auth.JwtAuthInterceptor`)
- **Redis backing store** via Lettuce (`io.lettuce.core.RedisClient`)
- **Coroutine-based service implementations** (`SessionServiceGrpcKt.*CoroutineImplBase`)

# ghostcache.api

A lightweight, high‑performance **gRPC cache service** backed by **Redis**, secured with **JWT Bearer authentication**. Designed for session storage and small binary payload caching with low latency and simple deployment.

---

## What this service does

* Exposes a **gRPC API** for storing, retrieving, and deleting session data
* Persists data in **Redis** using string keys and binary values
* Requires a **valid JWT** on every request
* Automatically selects the fastest available Netty transport:

  * `io_uring` (Linux, best case)
  * `epoll` (Linux fallback)
  * `NIO` (portable fallback)

---

## gRPC API

### SessionService

```proto
rpc PutSession(PutSessionRequest) returns (PutSessionReply);
rpc GetSession(GetSessionRequest) returns (GetSessionReply);
rpc DeleteSession(DeleteSessionRequest) returns (DeleteSessionReply);
```

| Method          | Behavior                                    |
| --------------- | ------------------------------------------- |
| `PutSession`    | Stores binary payload with TTL (default 1h) |
| `GetSession`    | Returns payload if key exists               |
| `DeleteSession` | Deletes the session key                     |

Payloads are raw bytes. TTL is enforced via Redis `SETEX`.

---

## Authentication

All requests **must** include a gRPC metadata header:

```
authorization: Bearer <JWT>
```

* Tokens are validated using an **HMAC secret** (`JWT_SECRET`)
* Invalid or missing tokens result in `UNAUTHENTICATED`
* JWT claims are injected into the gRPC `Context` for downstream use

---

## Required environment variables

| Variable       | Required | Description                    |
| -------------- | -------- | ------------------------------ |
| `REDIS_URL`    | ✅        | Redis connection URI           |
| `JWT_SECRET`   | ✅        | HMAC secret for JWT validation |
| `GRPC_PORT`    | ❌        | gRPC port (default `50051`)    |
| `USE_IO_URING` | ❌        | Set to `1` to prefer io_uring  |

Example Redis URLs:

* `redis://localhost:6379`
* `redis://:password@host:6379/0`


> Redis is expected to run **externally** (container, VM, or managed service).


## Performance & limits

* Max inbound gRPC message size: **1 MiB**
* Default Redis timeout: **2 seconds**
* Fixed worker executor (4 threads)
* Graceful shutdown of gRPC, Redis, and Netty event loops



## Typical use cases

* Session storage for APIs or gateways
* Short‑lived authentication/session tokens
* Small binary cache

---

## Notes

* Payloads are binary; clients must handle encoding/decoding
* Secrets should be injected via Docker/Kubernetes secrets
* For production, add metrics and health checks

---

## License

(Add your license here)
