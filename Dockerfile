# Multi-stage Dockerfile for building and running the Spring Boot app

# --- Build stage ---
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy project files from the subfolder Math-web-vs
COPY Math-web-vs/pom.xml .
COPY Math-web-vs/src ./src

# Build the JAR (skip tests)
RUN mvn -DskipTests -B package


# --- Runtime stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Default Render port
ENV PORT=10000
EXPOSE 10000

# Start the Spring Boot app
CMD ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar /app/app.jar"]
