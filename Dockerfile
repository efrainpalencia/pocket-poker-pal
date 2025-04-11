# Use official JDK image
FROM eclipse-temurin:17-jdk

# Author label
LABEL authors="EFAITECH SOLUTIONS, LLC"

# Set working directory
WORKDIR /app

# Copy Maven wrapper and configuration files first for better caching
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (caches better)
RUN ./mvnw dependency:go-offline

# Copy the rest of the application source
COPY src ./src

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Use a lightweight JDK base image to run the app (optional optimization)
# FROM eclipse-temurin:17-jdk-alpine
# WORKDIR /app
# COPY --from=0 /app/target/*.jar app.jar
# CMD ["java", "-jar", "app.jar"]

# Run the app (simple version)
CMD ["java", "-jar", "$(find target -name '*.jar' | head -n 1)"]
