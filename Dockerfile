FROM amazoncorretto:latest
WORKDIR /opt/cdt

COPY target/oraculo.jar .

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/oraculo.jar"]