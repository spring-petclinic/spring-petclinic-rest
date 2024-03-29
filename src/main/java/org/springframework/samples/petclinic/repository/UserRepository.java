package org.springframework.samples.petclinic.repository;

import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.User;

public interface UserRepository {

    User save(User user) throws DataAccessException;

    Optional<User> findByUsername(String username); 

}
