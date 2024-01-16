FROM maven:3.8.4-openjdk-17 as build
#ENV TESTCONTAINERS_RYUK_DISABLED="true"
ENV DOCKER_HOST http://dind:2376
COPY src src
COPY pom.xml .
RUN mvn clean install -DskipTests

FROM openjdk:17
COPY --from=build /target/login-service-0.0.1-SNAPSHOT.jar /target/login-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/target/login-service-0.0.1-SNAPSHOT.jar"]