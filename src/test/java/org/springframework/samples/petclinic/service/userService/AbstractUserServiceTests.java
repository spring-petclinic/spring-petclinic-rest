package org.springframework.samples.petclinic.service.userService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.UserService;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractUserServiceTests {

    @Autowired
    private UserService userService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldAddUser() throws Exception {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        user.addRole("OWNER_ADMIN");

        userService.saveUser(user);
        assertThat(user.getRoles().parallelStream().allMatch(role -> role.getName().startsWith("ROLE_")), is(true));
        assertThat(user.getRoles().parallelStream().allMatch(role -> role.getUser() != null), is(true));
    }
}
