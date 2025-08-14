#!/bin/sh
set -euo pipefail

# ---- Safety checks ----------------------------------------------------------
: "${JWT_SECRET:?JWT_SECRET must be set (use fly secrets)}"
: "${REDIS_PASSWORD:?REDIS_PASSWORD must be set (use fly secrets)}"

# ---- System tuning ----------------------------------------------------------
ulimit -n "${ULIMIT_NOFILE:-65535}"

# ---- Redis secure local config ---------------------------------------------
cat >/tmp/redis.conf <<EOF
bind 127.0.0.1
protected-mode yes
port 6379
requirepass ${REDIS_PASSWORD}
save ""
appendonly no
EOF

redis-server /tmp/redis.conf &
REDIS_PID=$!

# Wait for Redis to be ready
i=0
until redis-cli -a "$REDIS_PASSWORD" -h 127.0.0.1 -p 6379 ping >/dev/null 2>&1; do
  i=$((i+1)); [ $i -gt 100 ] && { echo "Redis failed to start"; exit 1; }
  sleep 0.1
done

# Provide REDIS_URL to the app (no secrets in fly.toml)
export REDIS_URL="redis://default:${REDIS_PASSWORD}@localhost:6379"

# ---- JVM flags tuned for containers ----------------------------------------
JAVA_OPTS="${JAVA_OPTS:-} \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=70 \
  -XX:InitialRAMPercentage=15 \
  -XX:MaxDirectMemorySize=128m \
  -XX:+ExitOnOutOfMemoryError"

# Graceful shutdown for both processes
term() { kill -TERM "$APP_PID" 2>/dev/null || true; kill -TERM "$REDIS_PID" 2>/dev/null || true; }
trap term INT TERM

# Start the app (don’t exec so traps still work)
java $JAVA_OPTS -jar /app/app.jar &
APP_PID=$!

# Wait for either to exit, then clean up
wait -n "$APP_PID" "$REDIS_PID"
status=$?

kill -TERM "$APP_PID" "$REDIS_PID" 2>/dev/null || true
wait "$APP_PID" 2>/dev/null || true
wait "$REDIS_PID" 2>/dev/null || true
exit $status
