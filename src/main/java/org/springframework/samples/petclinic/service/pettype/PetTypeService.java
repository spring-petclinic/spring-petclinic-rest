package org.springframework.samples.petclinic.service.pettype;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.PetType;

public interface PetTypeService {

    PetType findById(int id) throws DataAccessException;

    Collection<PetType> findAll() throws DataAccessException;

    Collection<PetType> findTypesForPets() throws DataAccessException;

    void save(PetType petType) throws DataAccessException;

    void delete(PetType petType) throws DataAccessException;
}
