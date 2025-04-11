# Use a base image that includes Maven and JDK
FROM maven:3.9.4-eclipse-temurin-17 as builder

# Author
LABEL authors="EFAITECH SOLUTIONS, LLC"

# Set work directory
WORKDIR /app

# Copy project files
COPY . .

# Pre-fetch dependencies (optional, speeds up build)
RUN mvn dependency:go-offline

# Build project
RUN mvn clean package -DskipTests

# ---- Runtime Image ----
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy built jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Run the app
CMD ["java", "-jar", "app.jar"]
