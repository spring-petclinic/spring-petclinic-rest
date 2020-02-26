/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.rest;

import java.io.IOException;

import org.springframework.samples.petclinic.model.Owner;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author Vitaliy Fedoriv
 *
 */

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
		String firstName = node.get("firstName").asText(null);
		String lastName = node.get("lastName").asText(null);
		String address = node.get("address").asText(null);
		String city = node.get("city").asText(null);
		String telephone = node.get("telephone").asText(null);
		if (node.hasNonNull("id")) {
			owner.setId(node.get("id").asInt());
		}
        owner.setFirstName(firstName);
        owner.setLastName(lastName);
        owner.setAddress(address);
        owner.setCity(city);
        owner.setTelephone(telephone);
		return owner;
	}

}
