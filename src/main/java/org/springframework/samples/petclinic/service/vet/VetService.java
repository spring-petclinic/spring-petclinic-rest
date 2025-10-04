package org.springframework.samples.petclinic.service.vet;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Vet;

public interface VetService {

    Vet findById(int id) throws DataAccessException;

    Collection<Vet> findAll() throws DataAccessException;

    void save(Vet vet) throws DataAccessException;

    void delete(Vet vet) throws DataAccessException;
}
