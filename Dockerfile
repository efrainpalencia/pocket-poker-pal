# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /build

# Pre-fetch dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the full project
COPY src ./src

# Package the Spring Boot app
RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:21-jdk
ARG PROFILE=prod
ARG VERSION=0.0.1

WORKDIR /app

# Copy the built JAR
COPY --from=build /build/target/*jar app.jar

EXPOSE 80
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${VERSION}

# Run with active profile
CMD ["java", "-Dspring.profiles.active=${ACTIVE_PROFILE}", "-jar", "app.jar"]
