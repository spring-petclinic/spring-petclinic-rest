package org.springframework.samples.petclinic.service;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Vet;

import java.util.Collection;
import java.util.Optional;

public interface VetService {
    Optional<Vet> findVetById(int id) throws DataAccessException;
    Collection<Vet> findVets() throws DataAccessException;
    Collection<Vet> findAllVets() throws DataAccessException;
    void saveVet(Vet vet) throws DataAccessException;
    void deleteVet(Vet vet) throws DataAccessException;
}
