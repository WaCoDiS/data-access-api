#build
FROM maven:3.5.4-jdk-8-alpine as BUILD

COPY . /app
RUN mvn -f /app/pom.xml clean package -DskipTests -Dapp.finalName=data-access-api


#run
FROM adoptopenjdk/openjdk8:alpine

COPY --from=BUILD /app/data-access-api/target/data-access-api.jar /app.jar
EXPOSE 8080

CMD ["java", "-jar", "/app.jar"]