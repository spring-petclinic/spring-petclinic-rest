# Use an official OpenJDK runtime as a parent image
FROM openjdk:19

# Set the working directory to /app
WORKDIR /app

# Copy the packaged jar file to the container
COPY target/spring-petclinic-rest-3.0.2.jar app.jar

# Expose the port on which the application will run
EXPOSE 9966

# Start the application
CMD ["java", "-jar", "app.jar"]
