# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Debug: List files before build
RUN echo "Files in /app:" && ls -la

# Run Maven clean package, skipping tests
RUN mvn clean package -DskipTests

# Debug: List generated JAR files in target directory
RUN echo "Generated JAR files:" && ls -la target/

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file from the builder stage (irrespective of name)
COPY --from=builder /app/target/*.jar app.jar

# Debug: List copied JAR file
RUN echo "Copied JAR file:" && ls -la

# Expose the application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]