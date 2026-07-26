# Etapa 1: Build - Usando Gradle 8.14 (o superior)
FROM gradle:8.14-jdk17-alpine AS build

WORKDIR /app

# Copiar los archivos desde la carpeta crawler
COPY crawler/build.gradle crawler/settings.gradle ./

# Copiar el gradle wrapper
COPY crawler/gradlew crawler/gradle ./gradle/

# Copiar código fuente
COPY crawler/src src

# Descargar dependencias y construir
RUN gradle build --no-daemon

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiar el JAR
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:hackernewsdb
ENV SPRING_H2_CONSOLE_ENABLED=true

ENTRYPOINT ["java", "-jar", "app.jar"]