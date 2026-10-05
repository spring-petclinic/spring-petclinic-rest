package org.springframework.samples.petclinic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Role extends BaseEntity {

    @JsonIgnore
    private User user;

    private String name;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
