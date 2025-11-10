# Multi-stage Dockerfile for building and running the Spring Boot app
# Build stage: use Maven to produce the fat jar
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Build the jar (skip tests for faster build in CI/containers)
RUN mvn -DskipTests -B package

# Runtime stage: smaller JRE image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the fat jar produced by the builder stage. Using a wildcard makes this
# resilient to minor changes in the final artifact name.
COPY --from=builder /app/target/*.jar app.jar

# Default PORT environment variable used by Render. Render will override $PORT.
ENV PORT=10000
EXPOSE 10000

# Start the Spring Boot application and bind it to the provided port
CMD ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar /app/app.jar"]
