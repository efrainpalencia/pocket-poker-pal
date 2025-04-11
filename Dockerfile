FROM eclipse-temurin:17-jdk

LABEL authors="EFAITECH SOLUTIONS, LLC"

WORKDIR /app

# Copy everything including .mvn and mvnw
COPY . .

# Ensure mvnw is executable inside the container
RUN chmod +x ./mvnw
RUN ls -l ./mvnw

# Prepare dependencies and build the app
RUN ./mvnw dependency:go-offline
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/*.jar"]
