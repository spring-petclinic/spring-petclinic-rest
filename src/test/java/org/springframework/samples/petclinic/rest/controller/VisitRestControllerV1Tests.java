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

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.VisitMapper;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class VisitRestControllerV1Tests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private VisitMapper visitMapper;

    @MockitoBean
    private VisitRepository visitRepository;

    private List<Visit> visits;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @BeforeEach
    void initVisits() {
        visits = new ArrayList<>();

        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("Eduardo");
        owner.setLastName("Rodriquez");
        owner.setAddress("2693 Commerce St.");
        owner.setCity("McFarland");
        owner.setTelephone("6085558763");

        PetType petType = new PetType();
        petType.setId(2);
        petType.setName("dog");

        Pet pet = new Pet();
        pet.setId(8);
        pet.setName("Rosy");
        pet.setBirthDate(LocalDate.now());
        pet.setOwner(owner);
        pet.setType(petType);

        Visit visit = new Visit();
        visit.setId(2);
        visit.setPet(pet);
        visit.setDate(LocalDate.now());
        visit.setDescription("rabies shot");
        visits.add(visit);

        visit = new Visit();
        visit.setId(3);
        visit.setPet(pet);
        visit.setDate(LocalDate.now());
        visit.setDescription("neutered");
        visits.add(visit);
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetVisitSuccess() throws Exception {
        given(visitRepository.findById(2)).willReturn(visits.get(0));
        this.mockMvc.perform(get("/api/visits/2")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.description").value("rabies shot"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetVisitNotFound() throws Exception {
        given(visitRepository.findById(999)).willReturn(null);
        this.mockMvc.perform(get("/api/visits/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllVisitsSuccess() throws Exception {
        given(visitRepository.findAll()).willReturn(visits);
        this.mockMvc.perform(get("/api/visits")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(2))
            .andExpect(jsonPath("$.[0].description").value("rabies shot"))
            .andExpect(jsonPath("$.[1].id").value(3))
            .andExpect(jsonPath("$.[1].description").value("neutered"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testGetAllVisitsNotFound() throws Exception {
        visits.clear();
        given(visitRepository.findAll()).willReturn(visits);
        this.mockMvc.perform(get("/api/visits")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateVisitSuccess() throws Exception {
        Visit newVisit = visits.get(0);
        newVisit.setId(999);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
        System.out.println("newVisitAsJSON " + newVisitAsJSON);
        this.mockMvc.perform(post("/api/visits")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testCreateVisitError() throws Exception {
        Visit newVisit = visits.get(0);
        newVisit.setId(null);
        newVisit.setDescription(null);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
        this.mockMvc.perform(post("/api/visits")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateVisitSuccess() throws Exception {
        given(visitRepository.findById(2)).willReturn(visits.get(0));
        Visit newVisit = visits.get(0);
        newVisit.setDescription("rabies shot test");
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
        this.mockMvc.perform(put("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/visits/2")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.description").value("rabies shot test"));
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testUpdateVisitError() throws Exception {
        Visit newVisit = visits.get(0);
        newVisit.setDescription(null);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
        this.mockMvc.perform(put("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeleteVisitSuccess() throws Exception {
        Visit newVisit = visits.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
        given(visitRepository.findById(2)).willReturn(visits.get(0));
        this.mockMvc.perform(delete("/api/visits/2")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void testDeleteVisitError() throws Exception {
        Visit newVisit = visits.get(0);
        ObjectMapper mapper = new ObjectMapper();
        String newVisitAsJSON = mapper.writeValueAsString(visitMapper.toVisitDto(newVisit));
        given(visitRepository.findById(999)).willReturn(null);
        this.mockMvc.perform(delete("/api/visits/999")
                .content(newVisitAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }
}
