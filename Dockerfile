FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

COPY ./wallet /app/wallet

EXPOSE 80

ENTRYPOINT [ "java", "-Dspring.profiles.active-prod", "-jar", "app.jar" ]