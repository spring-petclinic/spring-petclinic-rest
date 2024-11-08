/*
 * Copyright 2016-2017 the original author or authors.
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

package org.springframework.samples.petclinic.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.PetMapper;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for {@link PetRestController}
 *
 * @author Vitaliy Fedoriv
 */

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class PetRestControllerTests {

    @MockBean
    protected ClinicService clinicService;
    @Autowired
    private PetRestController petRestController;
    @Autowired
    private PetMapper petMapper;
    private MockMvc mockMvc;

    private List<PetDto> pets;

    @BeforeEach
    void initPets() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(petRestController)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();
        pets = new ArrayList<>();

        OwnerDto owner = new OwnerDto();
        owner.id(1).firstName("Eduardo")
            .lastName("Rodriquez")
            .address("2693 Commerce St.")
            .city("McFarland")
            .telephone("6085558763");

        PetTypeDto petType = new PetTypeDto();
        petType.id(2)
            .name("dog");

        PetDto pet = new PetDto();
        pets.add(pet.id(3)
            .name("Rosy")
            .birthDate(LocalDate.now())
            .type(petType));

        pet = new PetDto();
        pets.add(pet.id(4)
            .name("Jewel")
            .birthDate(LocalDate.now())
            .type(petType));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetPetSuccess() throws Exception {
        given(this.clinicService.findPetById(3)).willReturn(petMapper.toPet(pets.get(0)));
        this.mockMvc.perform(get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Rosy"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetPetNotFound() throws Exception {
        given(petMapper.toPetDto(this.clinicService.findPetById(-1))).willReturn(null);
        this.mockMvc.perform(get("/api/pets/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllPetsSuccess() throws Exception {
        final Collection<Pet> pets = petMapper.toPets(this.pets);
        System.err.println(pets);
        when(this.clinicService.findAllPets()).thenReturn(pets);
        //given(this.clinicService.findAllPets()).willReturn(petMapper.toPets(pets));
        this.mockMvc.perform(get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(3))
            .andExpect(jsonPath("$.[0].name").value("Rosy"))
            .andExpect(jsonPath("$.[1].id").value(4))
            .andExpect(jsonPath("$.[1].name").value("Jewel"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllPetsNotFound() throws Exception {
        pets.clear();
        given(this.clinicService.findAllPets()).willReturn(petMapper.toPets(pets));
        this.mockMvc.perform(get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdatePetSuccess() throws Exception {
        given(this.clinicService.findPetById(3)).willReturn(petMapper.toPet(pets.get(0)));
        PetDto newPet = pets.get(0);
        newPet.setName("Rosy I");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String newPetAsJSON = mapper.writeValueAsString(newPet);
        this.mockMvc.perform(put("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Rosy I"));

    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdatePetError() throws Exception {
        PetDto newPet = pets.get(0);
        newPet.setName(null);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String newPetAsJSON = mapper.writeValueAsString(newPet);

        this.mockMvc.perform(put("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeletePetSuccess() throws Exception {
        PetDto newPet = pets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findPetById(3)).willReturn(petMapper.toPet(pets.get(0)));
        this.mockMvc.perform(delete("/api/pets/3")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeletePetError() throws Exception {
        PetDto newPet = pets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findPetById(999)).willReturn(null);
        this.mockMvc.perform(delete("/api/pets/999")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testAddPetSuccess() throws Exception {
        PetDto newPet = pets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findPetById(3)).willReturn(petMapper.toPet(pets.get(0)));
        this.mockMvc.perform(post("/api/pets")
                .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated())
            .andExpect(header().string(HttpHeaders.LOCATION, "/api/pets/3"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testAddPetError() throws Exception {
        PetDto newPet = pets.get(0);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String newPetAsJSON = mapper.writeValueAsString(newPet);
        given(this.clinicService.findPetById(999)).willReturn(null);
        this.mockMvc.perform(post("/api/pets")
                // set empty JSON to force 400 error
                .content("{}").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }
}
