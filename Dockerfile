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

# Install bash and curl for wait script
RUN apk add --no-cache bash curl

# Set working directory
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Copy wait script
COPY wait-for-mysql.sh .
RUN chmod +x wait-for-mysql.sh

# Debug: List copied files
RUN echo "Copied files:" && ls -la

# Expose the application port
EXPOSE 8080

# Run the wait script before starting the app
CMD ["./wait-for-mysql.sh", "mysql-db:3306", "--", "java", "-Dspring.config.location=/app/config/", "-jar", "app.jar"]