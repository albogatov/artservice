FROM maven:3.8.4-openjdk-17 as build
COPY src src
COPY pom.xml .
RUN mvn clean install -DskipTests

FROM openjdk:17
COPY --from=build /target/profile-service-0.0.1-SNAPSHOT.jar /target/profile-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/target/profile-service-0.0.1-SNAPSHOT.jar"]