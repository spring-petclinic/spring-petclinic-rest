FROM openjdk:latest
COPY ./src/main/java/org/springframework/samples/petclinic/PetClinicApplication.java/ /tmp       
WORKDIR /tmp
EXPOSE 9966
ENTRYPOINT ["java","PetClinicApplication.java"]







