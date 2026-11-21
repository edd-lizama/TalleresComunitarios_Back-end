# --- Etapa 1: Compilación con Maven y JDK 21 ---
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
# Copiar wrapper de Maven y pom.xml primero para aprovechar la caché de dependencias
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Convertir saltos de línea Windows (CRLF) a Linux (LF) y dar permisos de ejecución
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
# Descargar dependencias offline
RUN ./mvnw dependency:go-offline -B
# Copiar código fuente y compilar el JAR ejecutando sin tests
COPY src ./src
RUN ./mvnw clean package -DskipTests
# --- Etapa 2: Imagen ligera de ejecución con JRE 21 ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Crear usuario sin privilegios de root por seguridad
RUN addgroup -S app && adduser -S app -G app
# Copiar el JAR compilado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar
USER app
# Puerto configurado en application.yml de service-auth-usuarios
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]