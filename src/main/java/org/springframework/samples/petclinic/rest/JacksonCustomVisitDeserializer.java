package org.springframework.samples.petclinic.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JacksonCustomVisitDeserializer extends StdDeserializer<Visit> {

	public JacksonCustomVisitDeserializer() {
		this(null);
	}

	public JacksonCustomVisitDeserializer(Class<Visit> t) {
		super(t);
	}

	@Override
	public Visit deserialize(JsonParser parser, DeserializationContext context)	throws IOException, JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		Visit visit = new Visit();
		Pet pet = new Pet();
		ObjectMapper mapper = new ObjectMapper();
		Date visitDate = null;
		JsonNode node = parser.getCodec().readTree(parser);
		JsonNode pet_node = node.get("pet");
		pet = mapper.treeToValue(pet_node, Pet.class);
		int visitId = node.get("id").asInt();
		String visitDateStr = node.get("date").asText(null);
		String description = node.get("description").asText(null);
		try {
			visitDate = formatter.parse(visitDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IOException(e);
		}

		if (!(visitId == 0)) {
			visit.setId(visitId);
		}
		visit.setDate(visitDate);
		visit.setDescription(description);
		visit.setPet(pet);
		return visit;
	}

}
