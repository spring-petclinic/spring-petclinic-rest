FROM maven:3.9.9-eclipse-temurin-17 AS buildstage
WORKDIR /app
COPY . .
RUN mvn clean package -Dskiptests

FROM openjdk:17-jdk-slim AS runstage
WORKDIR /app
COPY --from=buildstage /app/target/spring-petclinic-rest-3.4.3.jar petclinic.jar
EXPOSE 9966
CMD ["java", "-jar", "petclinic.jar"]
