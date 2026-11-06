# Usar una imagen base de Java 21 (igual a la de tu proyecto)
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copiar cualquier jar generado en target/ sin importar el nombre exacto
COPY target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]