FROM openjdk:25-ea-jdk
WORKDIR /APP

COPY target/YT-AI-MicroService-0.0.1-SNAPSHOT.jar /APP/YTAI.jar

ENTRYPOINT ["java" , "-jar" ,"YTAI.jar"]