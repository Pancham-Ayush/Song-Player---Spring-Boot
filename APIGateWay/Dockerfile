FROM openjdk:25-ea-jdk
WORKDIR /APP

COPY target/APIGateWay-0.0.1-SNAPSHOT.jar /APP/APIGATEWAY.jar
EXPOSE 8080

ENTRYPOINT ["java" , "-jar" ,"APIGATEWAY.jar"]