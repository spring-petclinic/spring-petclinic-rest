package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "OWNER_ADMIN")
class OwnerControllerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    // 🔥 HUGE COVERAGE BOOST
    @Test
    void shouldGetAllOwners() throws Exception {

        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk());
    }

    // 🔥 triggers validation + exception handler
    @Test
    void shouldFailCreateOwnerWhenFieldsMissing() throws Exception {

        String invalidJson = "{}";

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // 🔥 NOT FOUND branch
    @Test
    void shouldReturn404ForUnknownOwner() throws Exception {

        mockMvc.perform(get("/api/owners/999999"))
                .andExpect(status().isNotFound());
    }

    // 🔥 update branch
    @Test
    void shouldFailUpdateUnknownOwner() throws Exception {

        String json = """
                {
                  "firstName":"Test",
                  "lastName":"User",
                  "address":"street",
                  "city":"city",
                  "telephone":"1234567890"
                }
                """;

        mockMvc.perform(put("/api/owners/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isNotFound());
    }
}
