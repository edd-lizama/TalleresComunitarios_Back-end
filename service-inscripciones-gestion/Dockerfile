# Etapa 1: Compilación con Gradle y JDK 21
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar wrapper de Gradle y archivos de configuración primero (aprovecha caché de capas)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución al wrapper y descargar dependencias
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

# Copiar código fuente y compilar el JAR
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# Etapa 2: Imagen ligera de ejecución con JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa de compilación
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]