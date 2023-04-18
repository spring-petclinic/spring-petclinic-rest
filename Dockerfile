FROM openjdk:latest

# Add Maintainer Info

# Set the working directory
WORKDIR /app

# Copy the Spring PetClinic REST application jar to the container
COPY target/spring-petclinic-rest-3.0.2.jar /app

# Expose the port on which the application will run
EXPOSE 9966

# Run the application when the container starts
CMD ["java", "-jar", "spring-petclinic-rest-3.0.2.jar"]

