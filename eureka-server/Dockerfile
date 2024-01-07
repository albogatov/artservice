FROM maven:3.8.4-openjdk-17 as build
COPY src src
COPY pom.xml .
RUN mvn clean package

FROM openjdk:17
COPY --from=build /target/eureka-server.jar /target/eureka-server.jar
ENTRYPOINT ["java","-jar","/target/eureka-server.jar"]