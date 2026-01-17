FROM openjdk:25-ea-jdk
WORKDIR /APP

COPY target/SearchEngine-MicroService-0.0.1-SNAPSHOT.jar /APP/SEARCHENGINE.jar

ENTRYPOINT ["java" , "-jar" ,"SEARCHENGINE.jar"]