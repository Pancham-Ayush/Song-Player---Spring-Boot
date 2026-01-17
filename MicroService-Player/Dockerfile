FROM openjdk:25-ea-jdk
WORKDIR /APP

COPY target/MicroService-Player-0.0.1-SNAPSHOT.jar /APP/PLAYER.jar

ENTRYPOINT ["java" , "-jar" ,"PLAYER.jar"]