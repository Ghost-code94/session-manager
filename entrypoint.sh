#!/bin/sh
set -eu

# Optional: secure Redis a bit while keeping it simple
# If you set REDIS_PASSWORD via fly secrets, Redis will require it; otherwise it runs open (local only).
if [ -n "${REDIS_PASSWORD:-}" ]; then
  redis-server --bind 127.0.0.1 --protected-mode yes --save "" --appendonly no --requirepass "$REDIS_PASSWORD" &
else
  redis-server --bind 127.0.0.1 --protected-mode yes --save "" --appendonly no &
fi

# Wait for Redis readiness (best effort, don’t crash if redis-cli missing)
for i in 1 2 3 4 5 6 7 8 9 10; do
  if command -v redis-cli >/dev/null 2>&1; then
    if [ -n "${REDIS_PASSWORD:-}" ]; then
      redis-cli -h 127.0.0.1 -p 6379 -a "$REDIS_PASSWORD" ping >/dev/null 2>&1 && break
    else
      redis-cli -h 127.0.0.1 -p 6379 ping >/dev/null 2>&1 && break
    fi
  else
    break
  fi
  sleep 0.2
done

# Build REDIS_URL at runtime (don’t store creds in fly.toml)
if [ -n "${REDIS_PASSWORD:-}" ]; then
  export REDIS_URL="redis://default:${REDIS_PASSWORD}@localhost:6379"
else
  export REDIS_URL="redis://localhost:6379"
fi

# JVM container-friendly defaults (safe, won’t crash if unsupported)
JAVA_OPTS="${JAVA_OPTS:-} -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -XX:MaxDirectMemorySize=128m -XX:+ExitOnOutOfMemoryError"

# Start your app (exec = PID 1 inside tini)
exec java $JAVA_OPTS -jar /app/app.jar
