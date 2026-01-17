FROM openjdk:25-ea-jdk
WORKDIR /APP

COPY target/SecurityMicroService-0.0.1-SNAPSHOT.jar /APP/SECURITY.jar
EXPOSE 9090

ENTRYPOINT ["java" , "-jar" ,"SECURITY.jar"]