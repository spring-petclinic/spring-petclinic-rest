package org.springframework.samples.petclinic.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Owner;

import java.util.Collection;

public interface OwnerService {
    Owner findOwnerById(int id) throws DataAccessException;

    Collection<Owner> findAllOwners() throws DataAccessException;

    void saveOwner(Owner owner) throws DataAccessException;

    void deleteOwner(Owner owner) throws DataAccessException;

    Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException;
}
