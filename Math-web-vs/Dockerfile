# Use official OpenJDK image as the base image
FROM openjdk:17-jdk-slim

# Set working directory in container
WORKDIR /app

# Copy the Maven configuration file
COPY pom.xml .

# Copy the source code
COPY src ./src

# Copy Maven wrapper files if you have them
COPY mvnw .
COPY .mvn .mvn

# Build the application
RUN ./mvnw package -DskipTests

# Expose the port your app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/MathAssistant.jar"]
