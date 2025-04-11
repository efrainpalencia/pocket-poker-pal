FROM eclipse-temurin:17-jdk

LABEL authors="EFAITECH SOLUTIONS, LLC"

WORKDIR /app

# Copy and make the wrapper executable BEFORE other commands
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Now copy everything else
COPY . .

RUN ./mvnw dependency:go-offline
RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "target/*.jar"]
