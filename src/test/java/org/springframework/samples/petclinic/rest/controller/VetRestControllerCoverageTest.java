package org.springframework.samples.petclinic.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.samples.petclinic.rest.dto.VetDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VetRestControllerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    /**
     * 🔥 Covers listVets()
     */
    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void shouldListVets() throws Exception {

        mockMvc.perform(get("/api/vets"))
                .andExpect(status().isOk());
    }

    /**
     * 🔥 Covers getVet NOT_FOUND branch
     */
    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void shouldReturn404WhenVetMissing() throws Exception {

        mockMvc.perform(get("/api/vets/999999"))
                .andExpect(status().isNotFound());
    }

    /**
     * 🔥 MASSIVE COVERAGE BOOST
     * hits:
     * mapper
     * service
     * specialties branch
     * save
     */
    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void shouldCreateVet() throws Exception {

        VetDto vet = new VetDto();
        vet.setFirstName("Coverage");
        vet.setLastName("Boost");
        vet.setSpecialties(List.of()); // triggers zero-specialty branch

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(vet)))
                .andExpect(status().isCreated());
    }

    /**
     * 🔥 update NOT_FOUND branch
     */
    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void shouldReturn404WhenUpdatingMissingVet() throws Exception {

        VetDto vet = new VetDto();
        vet.setFirstName("Update");
        vet.setLastName("Fail");

        mockMvc.perform(put("/api/vets/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(vet)))
                .andExpect(status().isNotFound());
    }

    /**
     * 🔥 delete NOT_FOUND branch
     */
    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void shouldReturn404WhenDeletingMissingVet() throws Exception {

        mockMvc.perform(delete("/api/vets/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "VET_ADMIN")
    void shouldGetCreatedVet() throws Exception {

        String response = mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"A\",\"lastName\":\"B\"}"))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        mockMvc.perform(get(response))
                .andExpect(status().isOk());
    }

}
