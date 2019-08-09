FROM maven:3.6.1-jdk-8-slim AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM gcr.io/distroless/java  
COPY --from=build /usr/src/app/target/java-activemq-consumer-1.0.0-SNAPSHOT.jar /usr/app/java-activemq-consumer.jar  
ENTRYPOINT ["java","-jar","/usr/app/java-activemq-consumer.jar"]  
