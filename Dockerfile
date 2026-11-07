# --- Etapa 1: Compilar la aplicación con Maven ---
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copiar archivos de Maven y el código fuente
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Dar permisos de ejecución a mvnw y compilar omitiendo los tests
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# --- Etapa 2: Imagen final ligera para ejecución ---
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiar el jar compilado desde la primera etapa automáticamente
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]