#!/bin/sh
set -eu

# --- Require Redis auth -------------------------------------------------------
if [ -z "${REDIS_PASSWORD:-}" ]; then
  echo "ERROR: REDIS_PASSWORD must be set (use: fly secrets set REDIS_PASSWORD=...)" >&2
  exit 1
fi

# --- Start Redis (auth only) --------------------------------------------------
redis-server \
  --bind 127.0.0.1 \
  --protected-mode yes \
  --save "" \
  --appendonly no \
  --requirepass "$REDIS_PASSWORD" &

# --- Wait for Redis to be ready ----------------------------------------------
for i in 1 2 3 4 5 6 7 8 9 10; do
  if redis-cli -h 127.0.0.1 -p 6379 -a "$REDIS_PASSWORD" ping >/dev/null 2>&1; then
    break
  fi
  sleep 0.2
done

# --- Inject auth'd REDIS_URL for the app -------------------------------------
export REDIS_URL="redis://default:${REDIS_PASSWORD}@localhost:6379"

# --- JVM container-friendly defaults -----------------------------------------
JAVA_OPTS="${JAVA_OPTS:-} -XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -XX:MaxDirectMemorySize=128m -XX:+ExitOnOutOfMemoryError"

# --- Launch app ---------------------------------------------------------------
exec java $JAVA_OPTS -jar /app/app.jar
