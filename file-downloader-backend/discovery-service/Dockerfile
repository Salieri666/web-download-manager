FROM maven:3.9.15-amazoncorretto-21 as build
WORKDIR /build

COPY pom.xml pom.xml
COPY discovery-service/src discovery-service/src
COPY discovery-service/pom.xml discovery-service/pom.xml

RUN mvn -f discovery-service/pom.xml clean package -DskipTests=true -P kubernetes

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/discovery-service/target/*-filedownloader.jar /app/app.jar
CMD ["java", "-Dspring.profiles.active=sit,native", "-jar", "app.jar"]
