package org.springframework.samples.petclinic.service.pettype;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.PetTypeRepository;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({ "jdbc", "jpa", "spring-data-jpa" })
@Transactional(readOnly = true)
public class PetTypeServiceImpl implements PetTypeService {

    private final PetTypeRepository petTypeRepository;
    private final PetRepository petRepository;

    public PetTypeServiceImpl(PetTypeRepository petTypeRepository, PetRepository petRepository) {
        this.petTypeRepository = petTypeRepository;
        this.petRepository = petRepository;
    }

    @Override
    @Cacheable(value = "petTypes", key = "#id")
    public PetType findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> petTypeRepository.findById(id));
    }

    @Override
    @Cacheable(value = "petTypes", key = "'all'")
    public Collection<PetType> findAll() throws DataAccessException {
        return petTypeRepository.findAll();
    }

    @Override
    public Collection<PetType> findTypesForPets() throws DataAccessException {
        return petRepository.findPetTypes();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "petTypes", key = "#pet.id"),
            @CacheEvict(value = "petTypes", key = "'all'") })
    public void save(PetType petType) throws DataAccessException {
        petTypeRepository.save(petType);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "petTypes", key = "#pet.id"),
            @CacheEvict(value = "petTypes", key = "'all'") })
    public void delete(PetType petType) throws DataAccessException {
        petTypeRepository.delete(petType);
    }
}
