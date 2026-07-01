# --- Build stage ---
FROM maven:3.9-eclipse-temurin-23 AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN ./mvnw -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -q clean package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:23-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
