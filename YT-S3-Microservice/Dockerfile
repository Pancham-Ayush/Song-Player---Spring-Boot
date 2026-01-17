FROM openjdk:25-ea-jdk
WORKDIR /APP

COPY target/YT-S3-MicroService-0.0.1-SNAPSHOT.jar /APP/YTS3.jar

ENTRYPOINT ["java" , "-jar" ,"YTS3.jar"]