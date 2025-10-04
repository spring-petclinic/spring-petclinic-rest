package org.springframework.samples.petclinic.service.visit;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Visit;

public interface VisitService {

    Visit findById(int visitId) throws DataAccessException;

    Collection<Visit> findAll() throws DataAccessException;

    Collection<Visit> findByPetId(int petId);

    void save(Visit visit) throws DataAccessException;

    void delete(Visit visit) throws DataAccessException;
}
