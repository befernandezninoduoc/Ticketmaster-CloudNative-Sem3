FROM maven:3.9-eclipse-temurin-21 AS buildstage
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiar el jar generado por Maven
COPY target/TicketMaster-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]