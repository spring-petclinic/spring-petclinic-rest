package org.springframework.samples.petclinic.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
public class UserRestControllerTests {

    @Mock
    private UserService userService;

    @Autowired
    private UserRestController userRestController;

    private MockMvc mockMvc;

    @Before
    public void initVets() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userRestController)
            .setControllerAdvice(new ExceptionControllerAdvice()).build();
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testCreateUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        user.addRole( "OWNER_ADMIN" );
        ObjectMapper mapper = new ObjectMapper();
        String newVetAsJSON = mapper.writeValueAsString(user);
        this.mockMvc.perform(post("/api/users/")
            .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles="ADMIN")
    public void testCreateUserError() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        ObjectMapper mapper = new ObjectMapper();
        String newVetAsJSON = mapper.writeValueAsString(user);
        this.mockMvc.perform(post("/api/users/")
            .content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }
}
