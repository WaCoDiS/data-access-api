#build
FROM maven:3.5.4-jdk-8-alpine as BUILD

COPY . /app
RUN mvn -f /app/pom.xml clean package -DskipTests


#run
FROM openjdk:alpine

COPY --from=BUILD /app/data-access-api/target/data-access-api-0.0.1-SNAPSHOT.jar /app/data-access-api.jar
EXPOSE 8080

CMD ["java", "-jar", "/app/data-access-api.jar"]