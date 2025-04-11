# Use official JDK image
FROM eclipse-temurin:17-jdk

# Author
LABEL authors="EFAITECH SOLUTIONS, LLC"

# Set working directory
WORKDIR /app

# Copy everything into the container
COPY . .

# 💥 Add this line to force executable permission inside container
RUN chmod +x ./mvnw

# Prepare dependencies (optional but recommended)
RUN ./mvnw dependency:go-offline

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/*.jar"]
