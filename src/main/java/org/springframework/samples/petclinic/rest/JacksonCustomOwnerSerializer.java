package org.springframework.samples.petclinic.rest;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;

import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonCustomOwnerSerializer extends StdSerializer<Owner> {

	public JacksonCustomOwnerSerializer() {
		this(null);
	}

	public JacksonCustomOwnerSerializer(Class<Owner> t) {
		super(t);
	}

	@Override
	public void serialize(Owner owner, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		Format formatter = new SimpleDateFormat("yyyy/MM/dd");
		jgen.writeStartObject();
		if (owner.getId() == null) {
			jgen.writeNullField("id");
		} else {
			jgen.writeNumberField("id", owner.getId());
		}

		jgen.writeStringField("firstName", owner.getFirstName());
		jgen.writeStringField("lastName", owner.getLastName());
		jgen.writeStringField("address", owner.getAddress());
		jgen.writeStringField("city", owner.getCity());
		jgen.writeStringField("telephone", owner.getTelephone());
		// write pets array
		jgen.writeArrayFieldStart("pets");
		for (Pet pet : owner.getPets()) {
			jgen.writeStartObject(); // pet
			if (pet.getId() == null) {
				jgen.writeNullField("id");
			} else {
				jgen.writeNumberField("id", pet.getId());
			}
			jgen.writeStringField("name", pet.getName());
			jgen.writeStringField("birthDate", formatter.format(pet.getBirthDate()));

			PetType petType = pet.getType();
			jgen.writeObjectFieldStart("type");
			jgen.writeNumberField("id", petType.getId());
			jgen.writeStringField("name", petType.getName());
			jgen.writeEndObject(); // type

			jgen.writeNumberField("owner", pet.getOwner().getId());
			// write visits array
			jgen.writeArrayFieldStart("visits");
			for (Visit visit : pet.getVisits()) {
				jgen.writeStartObject(); // visit
				if (visit.getId() == null) {
					jgen.writeNullField("id");
				} else {
					jgen.writeNumberField("id", visit.getId());
				}
				jgen.writeStringField("date", formatter.format(visit.getDate()));
				jgen.writeStringField("description", visit.getDescription());
				jgen.writeNumberField("pet", visit.getPet().getId());
				jgen.writeEndObject(); // visit
			}
			jgen.writeEndArray(); // visits
			jgen.writeEndObject(); // pet
		}
		jgen.writeEndArray(); // pets
		jgen.writeEndObject(); // owner
	}

}
