############################################################
# Stage 1 — Build Spring Boot Application
############################################################
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Metadata
LABEL stage=builder

# Application build directory
WORKDIR /build

############################################################
# Copy Maven Dependency File First
# (Better Docker Layer Caching)
############################################################
COPY pom.xml .

############################################################
# Download Dependencies
############################################################
RUN mvn dependency:go-offline -B

############################################################
# Copy Source Code
############################################################
COPY src ./src

############################################################
# Build Application
############################################################
RUN mvn clean package \
    -DskipTests \
    -Dmaven.test.skip=true

############################################################
# Stage 2 — Tini Binary
############################################################
FROM ubuntu:24.04 AS tini

############################################################
# Install Tini
############################################################
RUN apt-get update && \
    apt-get install -y --no-install-recommends tini && \
    rm -rf /var/lib/apt/lists/*

############################################################
# Stage 3 — Production Runtime
############################################################
FROM gcr.io/distroless/java21-debian12:nonroot

############################################################
# OCI Image Labels
############################################################
LABEL org.opencontainers.image.title="springboot-service" \
      org.opencontainers.image.description="Production Spring Boot Application" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.vendor="RankX" 

############################################################
# Application Working Directory
############################################################
WORKDIR /app

############################################################
# Copy Tini Binary
############################################################
COPY --from=tini /usr/bin/tini /tini

############################################################
# Copy Spring Boot JAR
############################################################
COPY --from=build /build/target/*.jar app.jar

############################################################
# Use Distroless Non-Root User
#
# Distroless nonroot image already contains:
# UID  = 65532
# GID  = 65532
############################################################
USER 65532:65532

############################################################
# JVM Production Optimizations
############################################################
ENV JAVA_OPTS="\
-XX:+UseContainerSupport \
-XX:MaxRAMPercentage=75 \
-XX:+ExitOnOutOfMemoryError \
-Djava.security.egd=file:/dev/urandom \
-Djava.awt.headless=true"

############################################################
# Application Port
############################################################
EXPOSE 8086

############################################################
# Tini as PID 1
#
# Handles:
# - Zombie processes
# - Signal forwarding
# - Graceful shutdown
############################################################
ENTRYPOINT ["/tini", "--"]

############################################################
# Start Java Application
############################################################
CMD ["java", "-jar", "/app/app.jar"]