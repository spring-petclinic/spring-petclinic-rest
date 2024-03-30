package org.springframework.samples.petclinic.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class UserDetailServiceConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public UserDetailsManager userDetailsManager() {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

        // UserDetails user = User.builder()
        // .username("user2")
        // .password("11")
        // // .disabled(false)
        // .roles("USER")
        // .build();
        // UserDetails admin = User.builder()
        // .username("admin2")
        // .password("55")
        // // .disabled(false)
        // .roles("USER", "ADMIN")
        // .build();

        // users.createUser(user);
        // users.createUser(admin);

        return users;
    }

}
