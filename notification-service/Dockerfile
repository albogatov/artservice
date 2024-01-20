FROM maven:3.8.4-openjdk-17 as build
COPY src src
COPY pom.xml .
RUN mvn clean install -DskipTests

FROM openjdk:17
COPY --from=build /target/notification-service-0.0.1-SNAPSHOT.jar /target/notification-service-0.0.1-SNAPSHOT.jar
EXPOSE 5005
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:5005","-jar","/target/notification-service-0.0.1-SNAPSHOT.jar"]