package org.springframework.samples.petclinic.service.specialty;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public Specialty findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> specialtyRepository.findById(id));
    }

    @Override
    public Collection<Specialty> findAll() throws DataAccessException {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Specialty specialty) throws DataAccessException {
        specialtyRepository.save(specialty);
    }

    @Override
    @Transactional
    public void delete(Specialty specialty) throws DataAccessException {
        specialtyRepository.delete(specialty);
    }

    @Override
    public List<Specialty> findByNameIn(Set<String> names) throws DataAccessException {
        return EntityFinder.findOrNull(() -> specialtyRepository.findSpecialtiesByNameIn(names));
    }
}
