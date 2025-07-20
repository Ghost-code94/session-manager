# FROM maven:3.9.0-eclipse-temurin-17 AS build
# WORKDIR /app
# # Cache pom.xml and download dependencies
# COPY pom.xml .
# RUN mvn dependency:go-offline -B
# # Copy the complete source code and build the jar, skipping tests
# COPY src ./src
# RUN mvn --batch-mode clean package -DskipTests

# # Stage 2: Create a lightweight runtime image using a JRE
# FROM eclipse-temurin:17-jre-alpine
# # install Redis
# RUN apk add --no-cache redis
# WORKDIR /app

# # Set the default PORT environment variable so the application can read it.
# ENV GRPC_PORT=50051
# ARG REDIS_URL
# ENV REDIS_URL=$REDIS_URL
# ARG JWT_SECRET
# ENV JWT_SECRET=$JWT_SECRET

# # Copy the packaged JAR from the build stage
# COPY --from=build /app/target/cache-pipeline-0.0.1-SNAPSHOT.jar app.jar
# COPY entrypoint.sh /entrypoint.sh

# RUN chmod +x /entrypoint.sh

# # Expose the port defined by the PORT env variable (cannot use env in EXPOSE, so we use the default value)
# EXPOSE 50051

# # single ENTRYPOINT that launches both Redis and your Ktor+gRPC service
# ENTRYPOINT ["/entrypoint.sh"]

############################
# 1 ─── Build the fat JAR ──
############################
FROM maven:3.9.0-eclipse-temurin-17 AS build

WORKDIR /app

# ‑‑ Cache dependencies first so they survive most code changes
COPY pom.xml .
RUN mvn -B dependency:go-offline

# ‑‑ Now copy the rest and build (skip tests for speed)
COPY src ./src
RUN mvn -B clean package -DskipTests

############################
# 2 ─── Create slim JRE  ───
############################
FROM eclipse-temurin:17-jdk-alpine AS jre

# jlink strips the JRE down to only the modules your app needs
RUN jlink \
     --add-modules java.base,java.logging,java.naming,java.security.jgss \
     --no-header-files --no-man-pages --strip-debug \
     --output /customjre

############################
# 3 ─── Final runtime ──────
############################
FROM alpine:3.20

# tini = proper signal handling; redis = in‑container store
RUN apk add --no-cache redis tini

# Copy the slimmed‑down JRE
COPY --from=jre /customjre /opt/jre
ENV PATH="/opt/jre/bin:${PATH}"

# Non‑root user (defence‑in‑depth)
RUN addgroup -S ghost && adduser -S ghost -G ghost
USER ghost

# ─── App files & config ─────────────────────────────────────
WORKDIR /app

# Default values (can be overridden at deploy time)
ENV GRPC_PORT=50051 \
    REDIS_URL=redis://localhost:6379 \
    JWT_SECRET=""

# Copy built JAR and the entrypoint script
COPY --from=build /app/target/cache-pipeline-*-SNAPSHOT.jar app.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

EXPOSE 50051
ENTRYPOINT ["/sbin/tini","--","/entrypoint.sh"]
