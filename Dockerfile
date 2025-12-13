
# Eclipse Temurin seems to be a popular and open-source distribution of the OpenJDK
# Saw as well that there is an Alpine version which is much smaller in size
FROM eclipse-temurin:21-jdk-alpine

# Setting the working directory inside the container
WORKDIR /app

# Copy everything from the current directory into the container
COPY . .

# Make the Maven wrapper script executable
RUN chmod +x ./mvnw

# Build the application using Maven
RUN ./mvnw clean package

# Create logs directory
RUN mkdir -p /app/logs

# The Spring Boot app is configured to run on port 9966
EXPOSE 9966

# Define volume for logs
VOLUME ["/app/logs"]

# Run the Spring Boot application
# Using the JAR file created during the build
ENTRYPOINT ["java", "-jar", "target/spring-petclinic-rest-3.4.3.jar"]
