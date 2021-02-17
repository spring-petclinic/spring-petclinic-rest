package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.model.Visit;

import java.io.IOException;

public class JacksonCustomVetDeserializer extends StdDeserializer<Vet> {

    protected JacksonCustomVetDeserializer() { this(null); }
    protected JacksonCustomVetDeserializer(Class<Vet> vc) {
        super(vc);
    }

    @Override
    public Vet deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Vet vet = new Vet();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int vetId = node.get("id").asInt();
        String firstName = node.get("firstName").asText(null);
        String lastName = node.get("lastName").asText(null);

        if(vetId != 0) {
            vet.setId(vetId);
        }
        vet.setFirstName(firstName);
        vet.setLastName(lastName);

        return vet;
    }
}
