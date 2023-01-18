FROM maven:latest AS build-stage
WORKDIR /build
COPY . . 

RUN mvn clean package

FROM openjdk:latest
WORKDIR /temp
COPY --from=build-stage /build/target/spring-petclinic-*.jar app.jar
EXPOSE 9966
ENTRYPOINT ["/usr/bin/java", "-jar", "app.jar"]

