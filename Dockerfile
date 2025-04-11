# Use official JDK image
FROM eclipse-temurin:17-jdk

# Author
LABEL authors="EFAITECH SOLUTIONS, LLC"

# Set working directory
WORKDIR /app

# Copy everything
COPY . .

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Go offline to speed up build
RUN ./mvnw dependency:go-offline

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/*.jar"]
