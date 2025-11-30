# Stage 1: Build the application (using JDK for compiling and packaging)
FROM eclipse-temurin:21-jdk-alpine AS builder

# Install Maven for the build process
RUN apk update && apk add --no-cache maven

# Set working directory for the build
WORKDIR /app

# Copy Maven configuration and source code
COPY pom.xml .
COPY src ./src

# Build the Spring Boot application (skipping tests for faster build)
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image (using JRE for minimal size)
FROM eclipse-temurin:21-jre-alpine

# Install necessary utilities for the wait script:
# 1. bash: to execute the shell script
# 2. curl: often used for health checks/HTTP connectivity
# 3. netcat-openbsd: provides the 'nc' command, commonly used for reliable TCP port checks
RUN apk update && apk add --no-cache bash curl netcat-openbsd

# Set working directory
WORKDIR /app

# Copy the generated application JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Copy the database wait script and make it executable
COPY wait-for-mysql.sh .
RUN chmod +x wait-for-mysql.sh

# Debug: List copied files to confirm structure
RUN echo "Copied files:" && ls -la

# Expose the application port
EXPOSE 8080

# Run the wait script before starting the main Java application.
# Increased timeout to 180 seconds (3 minutes) to allow for slow database initialization.
CMD ["./wait-for-mysql.sh", "mysql-db:3306", "-t", "180", "--", "java", "-Dspring.config.location=/app/config/", "-jar", "app.jar"]
