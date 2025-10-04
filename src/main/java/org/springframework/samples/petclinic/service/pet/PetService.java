package org.springframework.samples.petclinic.service.pet;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Pet;

public interface PetService {

    Pet findById(int id) throws DataAccessException;

    Collection<Pet> findAll() throws DataAccessException;

    void save(Pet pet) throws DataAccessException;

    void delete(Pet pet) throws DataAccessException;
}
