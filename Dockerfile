# syntax=docker/dockerfile:1.4

############################
# Stage 1: Build Dependencies
############################
FROM eclipse-temurin:17-jdk-jammy AS deps
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B -DskipTests

############################
# Stage 2: Build Application
############################
FROM deps AS build
COPY . .
RUN ./mvnw clean package -DskipTests && \
    cp target/*.jar app.jar

############################
# Stage 3: Extract Spring Boot Layers
############################
FROM eclipse-temurin:17-jdk-jammy AS extract
WORKDIR /app

COPY --from=build /app/app.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract --destination layers

############################
# Stage 4: Minimal Runtime
############################
FROM --platform=linux/amd64 gcr.io/distroless/java17-debian12:nonroot AS runtime

WORKDIR /app
USER nonroot

COPY --from=extract /app/layers/dependencies/ ./
COPY --from=extract /app/layers/snapshot-dependencies/ ./
COPY --from=extract /app/layers/spring-boot-loader/ ./
COPY --from=extract /app/layers/application/ ./

EXPOSE 9966
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
