package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "OWNER_ADMIN")
class HighCoverageSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    // Owner
    @Test
    void shouldReturn404ForMissingOwner() throws Exception {
        mockMvc.perform(get("/api/owners/999999"))
                .andExpect(status().isNotFound());
    }

    // Pet
    @Test
    void shouldReturn404ForMissingPet() throws Exception {
        mockMvc.perform(get("/api/pets/999999"))
                .andExpect(status().isNotFound());
    }

    // Delete pet
    @Test
    void shouldReturn404WhenDeletingMissingPet() throws Exception {
        mockMvc.perform(delete("/api/pets/999999"))
                .andExpect(status().isNotFound());
    }

    // Bad update
    @Test
    void shouldReturnBadRequestOnInvalidPetUpdate() throws Exception {
        mockMvc.perform(
                put("/api/pets/1")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
