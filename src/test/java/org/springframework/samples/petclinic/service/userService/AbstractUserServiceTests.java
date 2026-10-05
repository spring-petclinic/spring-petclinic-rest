package org.springframework.samples.petclinic.service.userService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.UserService;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;

public abstract class AbstractUserServiceTests {

    @Autowired
    private UserService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Test
    @Transactional
    public void shouldUpdateExistingUser() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("new-password");
        user.setEnabled(false);
        user.addRole("VET_ADMIN");

        userService.saveUser(user);
        assertThat(jdbcTemplate.queryForObject("SELECT password FROM users WHERE username = 'admin'", String.class),
            is("new-password"));
        assertThat(jdbcTemplate.queryForList("SELECT role FROM roles WHERE username = 'admin'", String.class),
            contains("ROLE_VET_ADMIN"));
    }
}
