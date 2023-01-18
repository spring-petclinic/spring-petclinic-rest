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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.mapper.SpecialtyMapper;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.samples.petclinic.rest.controller.SpecialtyRestController;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link SpecialtyRestController}
 *
 * @author Vitaliy Fedoriv
 */
@SpringBootTest
@ContextConfiguration(classes=ApplicationTestConfig.class)
@WebAppConfiguration
class SpecialtyRestControllerTests {

    @Autowired
    private SpecialtyRestController specialtyRestController;

    @Autowired
    private SpecialtyMapper specialtyMapper;

	@MockBean
    private ClinicService clinicService;

    private MockMvc mockMvc;

    private List<Specialty> specialties;

    @BeforeEach
    void initSpecialtys(){
    	this.mockMvc = MockMvcBuilders.standaloneSetup(specialtyRestController)
    			.setControllerAdvice(new ExceptionControllerAdvice())
    			.build();
    	specialties = new ArrayList<Specialty>();

    	Specialty specialty = new Specialty();
    	specialty.setId(1);
    	specialty.setName("radiology");
    	specialties.add(specialty);

    	specialty = new Specialty();
    	specialty.setId(2);
    	specialty.setName("surgery");
    	specialties.add(specialty);

    	specialty = new Specialty();
    	specialty.setId(3);
    	specialty.setName("dentistry");
    	specialties.add(specialty);

    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testGetSpecialtySuccess() throws Exception {
    	given(this.clinicService.findSpecialtyById(1)).willReturn(specialties.get(0));
        this.mockMvc.perform(get("/api/specialties/1")
        	.accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testGetSpecialtyNotFound() throws Exception {
    	given(this.clinicService.findSpecialtyById(999)).willReturn(null);
        this.mockMvc.perform(get("/api/specialties/999")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testGetAllSpecialtysSuccess() throws Exception {
    	specialties.remove(0);
    	given(this.clinicService.findAllSpecialties()).willReturn(specialties);
        this.mockMvc.perform(get("/api/specialties/")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
        	.andExpect(jsonPath("$.[0].id").value(2))
        	.andExpect(jsonPath("$.[0].name").value("surgery"))
        	.andExpect(jsonPath("$.[1].id").value(3))
        	.andExpect(jsonPath("$.[1].name").value("dentistry"));
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testGetAllSpecialtysNotFound() throws Exception {
    	specialties.clear();
    	given(this.clinicService.findAllSpecialties()).willReturn(specialties);
        this.mockMvc.perform(get("/api/specialties/")
        	.accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testCreateSpecialtySuccess() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	newSpecialty.setId(999);
    	ObjectMapper mapper = new ObjectMapper();
        String newSpecialtyAsJSON = mapper.writeValueAsString(specialtyMapper.toSpecialtyDto(newSpecialty));
    	this.mockMvc.perform(post("/api/specialties/")
    		.content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
    		.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testCreateSpecialtyError() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	newSpecialty.setId(null);
    	newSpecialty.setName(null);
    	ObjectMapper mapper = new ObjectMapper();
        String newSpecialtyAsJSON = mapper.writeValueAsString(specialtyMapper.toSpecialtyDto(newSpecialty));
    	this.mockMvc.perform(post("/api/specialties/")
        		.content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        		.andExpect(status().isBadRequest());
     }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testUpdateSpecialtySuccess() throws Exception {
    	given(this.clinicService.findSpecialtyById(2)).willReturn(specialties.get(1));
    	Specialty newSpecialty = specialties.get(1);
    	newSpecialty.setName("surgery I");
    	ObjectMapper mapper = new ObjectMapper();
        String newSpecialtyAsJSON = mapper.writeValueAsString(specialtyMapper.toSpecialtyDto(newSpecialty));
    	this.mockMvc.perform(put("/api/specialties/2")
    		.content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(content().contentType("application/json"))
        	.andExpect(status().isNoContent());

    	this.mockMvc.perform(get("/api/specialties/2")
           	.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("surgery I"));
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testUpdateSpecialtyError() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	newSpecialty.setName("");
    	ObjectMapper mapper = new ObjectMapper();
        String newSpecialtyAsJSON = mapper.writeValueAsString(specialtyMapper.toSpecialtyDto(newSpecialty));
    	this.mockMvc.perform(put("/api/specialties/1")
    		.content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isBadRequest());
     }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testDeleteSpecialtySuccess() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	ObjectMapper mapper = new ObjectMapper();
        String newSpecialtyAsJSON = mapper.writeValueAsString(specialtyMapper.toSpecialtyDto(newSpecialty));
    	given(this.clinicService.findSpecialtyById(1)).willReturn(specialties.get(0));
    	this.mockMvc.perform(delete("/api/specialties/1")
    		.content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles="VET_ADMIN")
    void testDeleteSpecialtyError() throws Exception {
    	Specialty newSpecialty = specialties.get(0);
    	ObjectMapper mapper = new ObjectMapper();
        String newSpecialtyAsJSON = mapper.writeValueAsString(specialtyMapper.toSpecialtyDto(newSpecialty));
    	given(this.clinicService.findSpecialtyById(999)).willReturn(null);
    	this.mockMvc.perform(delete("/api/specialties/999")
    		.content(newSpecialtyAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
        	.andExpect(status().isNotFound());
    }
}
