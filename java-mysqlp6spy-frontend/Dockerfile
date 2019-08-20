FROM maven:3.6.1-jdk-8-slim AS build  
WORKDIR /usr/src/app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn package

FROM gcr.io/distroless/java  
COPY --from=build /usr/src/app/target/java-mysqlp6spy-frontend-1.0.0-SNAPSHOT.jar /usr/app/java-mysqlp6spy-frontend.jar  
ENTRYPOINT ["java","-jar","/usr/app/java-mysqlp6spy-frontend.jar"]  
