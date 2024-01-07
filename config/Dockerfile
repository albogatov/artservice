FROM maven:3.8.4-openjdk-17 as build
COPY ./src src
COPY ./pom.xml .
RUN mvn clean package

FROM openjdk:17
COPY --from=build /target/config.jar /target/config.jar
ENTRYPOINT ["java","-jar","/target/config.jar"]