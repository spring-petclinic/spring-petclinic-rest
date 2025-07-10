package org.springframework.samples.petclinic.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.User;

import java.util.Collection;

public interface UserRepository {

    void save(User user) throws DataAccessException;

    Collection<User> findAll() throws DataAccessException;
}
