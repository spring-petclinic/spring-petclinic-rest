package org.springframework.samples.petclinic.rest.advice;

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
class ExceptionHandlerCoverageTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * ⭐ HUGE COVERAGE BOOST
     * Triggers validation exception -> ControllerAdvice
     */
    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void shouldReturn400WhenCreatingInvalidOwner() throws Exception {

        mockMvc.perform(post("/api/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    /**
     * ⭐ MASSIVE BRANCH BOOST
     * NOT FOUND -> advice path
     */
    @Test
    @WithMockUser(roles = "OWNER_ADMIN")
    void shouldReturn404ForUnknownOwner() throws Exception {

        mockMvc.perform(get("/api/owners/99999999"))
                .andExpect(status().isNotFound());
    }

}
