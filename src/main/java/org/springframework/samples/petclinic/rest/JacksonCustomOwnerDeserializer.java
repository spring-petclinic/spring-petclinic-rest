package org.springframework.samples.petclinic.rest;

import java.io.IOException;

import org.springframework.samples.petclinic.model.Owner;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;

public class JacksonCustomOwnerDeserializer extends StdDeserializer<Owner> {
	
	public JacksonCustomOwnerDeserializer(){
		this(null);
	}

	public JacksonCustomOwnerDeserializer(Class<Owner> t) {
		super(t);
	}

	@Override
	public Owner deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
		JsonNode node = parser.getCodec().readTree(parser);
		Owner owner = new Owner();
		int id = node.get("id").asInt();
		String firstName = node.get("firstName").asText(null);
		String lastName = node.get("lastName").asText(null);
		String address = node.get("address").asText(null);
		String city = node.get("city").asText(null);
		String telephone = node.get("telephone").asText(null);

		if (!(id == 0)) {
			owner.setId(id);
		}
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress(address);
        owner.setCity(city);
        owner.setTelephone(telephone);
		return owner;
	}

}
