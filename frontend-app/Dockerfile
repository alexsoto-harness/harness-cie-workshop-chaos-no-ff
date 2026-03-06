FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY target/*.jar app.jar

EXPOSE 4000

CMD ["java", "-jar", "app.jar"]
