# Dockerfile para compilar y ejecutar el backend Spring Boot
# El proyecto usa Java 20, pero JDK 21 puede compilar source/target 20 y ejecutar el .jar.

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

ENV PORT=8081

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
