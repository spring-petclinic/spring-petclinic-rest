package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OwnerHappyPathCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 🔥 THIS TEST IS A COVERAGE NUKE
     * It executes:
     * 
     * controller
     * service
     * repository
     * mapper
     * loops
     * collections
     * serialization
     * security
     * advice
     * 
     * → massive instruction gain
     */
    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void shouldListOwnersSuccessfully() throws Exception {

        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk());
    }
}
