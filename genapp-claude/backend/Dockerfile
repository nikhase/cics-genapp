# Multi-stage build for Spring Boot application
# Stage 1: Build the application with Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:resolve

# Copy source code and build
COPY src/ src/
RUN mvn clean package -DskipTests

# Stage 2: Runtime image (minimal footprint)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the compiled JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Create non-root user for security
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
