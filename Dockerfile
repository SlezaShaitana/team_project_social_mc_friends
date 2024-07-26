FROM openjdk:17.0
WORKDIR /app
COPY mc-friends/target/mc-friends-0.0.1-SNAPSHOT.jar /app/mc-friends.jar
ENTRYPOINT  ["java", "-jar", "mc-friends.jar"]