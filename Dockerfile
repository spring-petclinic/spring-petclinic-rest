FROM maven:3.8.4-jdk-8 AS build
COPY . .
RUN mvn clean package 
FROM openjdk:8
COPY --from=build /target/spring-petclinic-rest-2.6.2.jar app.jar
EXPOSE 9966
ENTRYPOINT ["java", "-jar", "app.jar"]


