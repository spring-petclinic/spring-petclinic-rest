package org.springframework.samples.petclinic.security.model;

public class LoginRes {
    private String email;
    private String token;

    public LoginRes(String email, String token) {
        this.email = email;
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

