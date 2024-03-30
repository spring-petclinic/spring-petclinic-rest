package org.springframework.samples.petclinic.security;

import java.util.Arrays;

public class UserDtoSecurity {

    private String usernamemail;

    private String password;

    private String[] roles;

    public String getUsernamemail() {
        return usernamemail;
    }

    public void setUsernamemail(String username) {
        this.usernamemail = username;
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
        return "UserDtoSecurity [username=" + usernamemail + ", password=" + password + ", roles="
                + Arrays.toString(roles)
                + "]";
    }

}
