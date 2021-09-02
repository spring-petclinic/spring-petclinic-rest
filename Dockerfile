FROM maven:3.8.2-openjdk-8 as build

WORKDIR /workspace

COPY . .

RUN mvn -Dmaven.test.skip=true package

RUN mkdir -p /opt/spring-petclinic && \
  mv target/spring-petclinic-rest-*.jar /opt/spring-petclinic/app.jar

FROM openjdk:10

RUN mkdir -p /opt/spring-petclinic

COPY --from=build /opt/spring-petclinic/*.jar /opt/spring-petclinic/

CMD ["sh", "-c", "java -jar /opt/spring-petclinic/app.jar"]

