FROM maven:3.9.9-eclipse-temurin-17 AS buildstage
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY . . 
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim AS runstage
WORKDIR /app
COPY --from=buildstage /app/target/spring-petclinic-rest-*.jar petclinic.jar
EXPOSE 9966
ENTRYPOINT ["java", "-jar", "petclinic.jar"]
