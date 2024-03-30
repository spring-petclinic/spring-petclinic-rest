package org.springframework.samples.petclinic.security.model;

import java.util.Arrays;

public class RegisterUserDto {

    private String username;

    private String password;

    private String[] roles;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserDtoSecurity [username=" + username + ", password=" + password + ", roles=" + Arrays.toString(roles)
                + "]";
    }

}
