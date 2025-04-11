# Use official JDK image
FROM eclipse-temurin:17-jdk

# Author
LABEL authors="EFAITECH SOLUTIONS, LLC"

# Set working directory
WORKDIR /app

# Copy Maven/Gradle wrapper and config files
COPY . .

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/*.jar"]
