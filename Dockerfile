# Multi-stage Dockerfile for building and running the Spring Boot app
FROM maven:3.8.8-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY Math-web/src ./src

RUN mvn -DskipTests -B package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV PORT=10000
EXPOSE 10000

CMD ["sh", "-c", "java -Dserver.port=${PORT:-10000} -jar /app/app.jar"]
