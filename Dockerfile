FROM openjdk:17-alpine
COPY target/hotel-booking-service-0.0.1-SNAPSHOT-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=heroku", "app.jar"]
