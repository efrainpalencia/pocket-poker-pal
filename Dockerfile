# Use official JDK image
FROM eclipse-temurin:17-jdk

LABEL authors="EFAITECH SOLUTIONS, LLC"

WORKDIR /app

# Copy the Maven wrapper first and set permission before copying the rest
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Now copy the rest of the project
COPY . .

# Preload dependencies
RUN ./mvnw dependency:go-offline

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/*.jar"]
