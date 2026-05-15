FROM maven:3.9.15-amazoncorretto-21 as build
WORKDIR /build

COPY pom.xml pom.xml
COPY config-service/src config-service/src
COPY config-service/pom.xml config-service/pom.xml

RUN mvn -f config-service/pom.xml clean package -DskipTests=true

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/config-service/target/*-filedownloader.jar /app/app.jar
CMD ["java", "-Dspring.profiles.active=sit,native", "-jar", "app.jar"]
