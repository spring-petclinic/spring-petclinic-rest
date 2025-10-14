package org.springframework.samples.petclinic.service.specialty;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({ "jdbc", "jpa", "spring-data-jpa" })
@Transactional(readOnly = true)
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    @Cacheable(value = "specialties", key = "#id")
    public Specialty findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> specialtyRepository.findById(id));
    }

    @Override
    @Cacheable(value = "specialties", key = "'all'")
    public Collection<Specialty> findAll() throws DataAccessException {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "specialties", key = "#specialty.id"),
            @CacheEvict(value = "specialties", key = "'all'") })
    public void save(Specialty specialty) throws DataAccessException {
        specialtyRepository.save(specialty);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "specialties", key = "#specialty.id"),
            @CacheEvict(value = "specialties", key = "'all'") })
    public void delete(Specialty specialty) throws DataAccessException {
        specialtyRepository.delete(specialty);
    }

    @Override
    public List<Specialty> findByNameIn(Set<String> names) throws DataAccessException {
        return EntityFinder.findOrNull(() -> specialtyRepository.findSpecialtiesByNameIn(names));
    }
}
