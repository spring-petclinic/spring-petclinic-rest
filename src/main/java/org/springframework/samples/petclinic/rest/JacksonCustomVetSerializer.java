package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.samples.petclinic.model.*;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

public class JacksonCustomVetSerializer extends StdSerializer<Vet> {

    protected JacksonCustomVetSerializer() { this(null); }
    protected JacksonCustomVetSerializer(Class<Vet> t) {
        super(t);
    }

    @Override
    public void serialize(Vet vet, JsonGenerator jgen, SerializerProvider serializerProvider) throws IOException {
        if (vet == null) {
            throw new IOException("Cannot serialize Vet object - vet is null");
        }
        Format formatter = new SimpleDateFormat("yyyy/MM/dd");
        jgen.writeStartObject(); // vet
        if(vet.getId() == null) {
            jgen.writeNullField("id");
        } else {
            jgen.writeNumberField("id", vet.getId());
        }
        jgen.writeStringField("firstName", vet.getFirstName());
        jgen.writeStringField("lastName", vet.getLastName());

        jgen.writeArrayFieldStart("specialties"); // specialties
        for (Specialty specialty : vet.getSpecialties()) {
            jgen.writeStartObject(); // specialty
            if(specialty.getId() == null) {
                jgen.writeNullField("id");
            } else {
                jgen.writeNumberField("id", specialty.getId());
            }
            jgen.writeStringField("name", specialty.getName());
            jgen.writeEndObject(); // specialty
        }
        jgen.writeEndArray(); // specialties

        jgen.writeArrayFieldStart("visits");
        for (Visit visit : vet.getVisits()) {
            jgen.writeStartObject(); //visit
            if(visit.getId() == null) {
                jgen.writeNullField("id");
            } else {
                jgen.writeNumberField("id", visit.getId());
            }
            jgen.writeStringField("date", formatter.format(visit.getDate()));
            jgen.writeStringField("description", visit.getDescription());

            Pet pet = visit.getPet();
            jgen.writeObjectFieldStart("pet"); // pet
            if (pet.getId() == null) {
                jgen.writeNullField("id");
            } else {
                jgen.writeNumberField("id", pet.getId());
            }
            jgen.writeStringField("name", pet.getName());

            Owner owner = pet.getOwner();
            jgen.writeObjectFieldStart("owner"); // owner
            if (pet.getId() == null) {
                jgen.writeNullField("id");
            } else {
                jgen.writeNumberField("id", owner.getId());
            }
            jgen.writeStringField("firstName", owner.getFirstName());
            jgen.writeStringField("lastName", owner.getLastName());
            jgen.writeEndObject(); // owner
            jgen.writeEndObject(); // pet
            jgen.writeEndObject(); // visit
        }
        jgen.writeEndArray(); // visits

        jgen.writeEndObject(); // vet
    }
}
