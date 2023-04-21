FROM openjdk:latest

WORKDIR /app

COPY target/spring-petclinic-rest-3.0.2.jar /app

EXPOSE 9966

CMD ["java", "-jar", "spring-petclinic-rest-3.0.2.jar"]
