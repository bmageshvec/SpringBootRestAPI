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
# Debug: List built artifacts
RUN echo "Built files in /app/target:" && ls -la /app/target/

# Stage 2: Create the runtime image (using JRE for minimal size)
FROM eclipse-temurin:21-jre-alpine
# Install necessary utilities for the wait script
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
# Set default timeout env (overridable in compose)
ENV WAIT_TIMEOUT=180
# Exec form for reliable arg parsing: wait script runs, then execs java
CMD ["./wait-for-mysql.sh", "mysql-db", "3306", "java", "-jar", "app.jar"]
