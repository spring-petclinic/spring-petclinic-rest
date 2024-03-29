package org.springframework.samples.petclinic.service;

import java.util.Optional;

import org.springframework.samples.petclinic.model.User;

public interface UserService {

    void saveUser(User user) ;

    Optional<User> getByUsername(String username); 
    
}
