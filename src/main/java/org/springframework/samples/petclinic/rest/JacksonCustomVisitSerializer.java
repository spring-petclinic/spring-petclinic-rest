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

public class JacksonCustomVisitSerializer extends StdSerializer<Visit> {

	public JacksonCustomVisitSerializer() {
		this(null);
	}

	protected JacksonCustomVisitSerializer(Class<Visit> t) {
		super(t);
	}

	@Override
	public void serialize(Visit visit, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		if ((visit == null) || (visit.getPet() == null)) {
			throw new IOException("Cannot serialize Visit object - visit or visit.pet is null");
		}
		Format formatter = new SimpleDateFormat("yyyy/MM/dd");
		jgen.writeStartObject(); // visit
		if (visit.getId() == null) {
			jgen.writeNullField("id");
		} else {
			jgen.writeNumberField("id", visit.getId());
		}
		jgen.writeStringField("date", formatter.format(visit.getDate()));
		jgen.writeStringField("description", visit.getDescription());

		Pet pet = visit.getPet();
		jgen.writeObjectFieldStart("pet");
		if (pet.getId() == null) {
			jgen.writeNullField("id");
		} else {
			jgen.writeNumberField("id", pet.getId());
		}
		jgen.writeStringField("name", pet.getName());
		jgen.writeStringField("birthDate", formatter.format(pet.getBirthDate()));

		PetType petType = pet.getType();
		jgen.writeObjectFieldStart("type");
		if (petType.getId() == null) {
			jgen.writeNullField("id");
		} else {
			jgen.writeNumberField("id", petType.getId());
		}
		jgen.writeStringField("name", petType.getName());
		jgen.writeEndObject(); // type

		Owner owner = pet.getOwner();
		jgen.writeObjectFieldStart("owner");
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
		jgen.writeEndObject(); // owner
		jgen.writeEndObject(); // pet
		jgen.writeEndObject(); // visit
	}

}
