package org.springframework.samples.petclinic.service;

import org.springframework.samples.petclinic.model.User;

import java.util.Collection;

public interface UserService {

    void saveUser(User user);

    Collection<User> findAllUsers();
}
