############################
# 1 ─── Build the fat JAR ──
############################
FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /app

# Cache Maven repo layers
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2,id=ghostcache-m2-cache \
    mvn -B dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2,id=ghostcache-m2-cache \
    mvn -B clean package -DskipTests

############################
# 2 ─── Create slim JRE  ───
############################
FROM eclipse-temurin:17-jdk-alpine AS jre
RUN jlink \
     --add-modules java.base,java.logging,java.naming,java.security.jgss \
     --no-header-files --no-man-pages --strip-debug \
     --output /customjre

############################
# 3 ─── Final runtime ──────
############################
FROM alpine:3.20

RUN apk add --no-cache redis tini

# Slimmed‑down JRE
COPY --from=jre /customjre /opt/jre
ENV PATH="/opt/jre/bin:${PATH}"

# Create dedicated user
RUN addgroup -S ghost -g 1000 && adduser -S ghost -G ghost -u 1000

WORKDIR /app
ENV GRPC_PORT=50051 \
    REDIS_URL=redis://localhost:6379

# Copy artefacts (exact filename → single source)
COPY --chown=ghost:ghost --from=build \
     /app/target/cache-pipeline-0.0.1-SNAPSHOT.jar /app/app.jar

COPY --chmod=755 --chown=ghost:ghost entrypoint.sh /entrypoint.sh

USER ghost

EXPOSE 50051
# ENTRYPOINT ["/sbin/tini","--","/entrypoint.sh"]
ENTRYPOINT ["/sbin/tini","-s","--","/entrypoint.sh"]
