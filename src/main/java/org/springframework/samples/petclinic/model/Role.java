package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "roles" ,uniqueConstraints = @UniqueConstraint(columnNames = {"username", "role"}))
public class Role extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "username")
    @JsonIgnore
    private User user;

    @Column( name = "role")
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
