FROM maven:3.5.4-jdk-8-alpine as maven

COPY ./pom.xml ./pom.xml
COPY ./src ./src

RUN mvn dependency:go-offline -B
RUN mvn package

FROM openjdk:8u171-jre-alpine

WORKDIR /petclinic-rest

COPY --from=maven target/spring-petclinic-rest-*.jar ./spring-petclinic-rest.jar
CMD ["java", "-jar", "./spring-petclinic-rest.jar"]

EXPOSE 9966