#build
FROM maven:3.5.4-jdk-8-alpine as BUILD

COPY /src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package


#run
FROM openjdk:alpine

COPY --from=BUILD /app/target/data-access-api-0.0.1-SNAPSHOT.jar /app/data-access-api.jar
EXPOSE 8080

CMD java -jar /app/data-access-api.jar