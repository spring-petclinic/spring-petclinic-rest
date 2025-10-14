package org.springframework.samples.petclinic.service.pet;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.service.pettype.PetTypeService;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({ "jdbc", "jpa", "spring-data-jpa" })
@Transactional(readOnly = true)
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetTypeService petTypeService;

    public PetServiceImpl(PetRepository petRepository, PetTypeService petTypeService) {
        this.petRepository = petRepository;
        this.petTypeService = petTypeService;
    }

    @Override
    @Cacheable(value = "pets", key = "#id")
    public Pet findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> petRepository.findById(id));
    }

    @Override
    @Cacheable(value = "pets", key = "'all'")
    public Collection<Pet> findAll() throws DataAccessException {
        return petRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "pets", key = "#pet.id"),
            @CacheEvict(value = "pets", key = "'all'") })
    public void save(Pet pet) throws DataAccessException {
        if (pet.getType() != null && pet.getType().getId() != null) {
            PetType resolved = petTypeService.findById(pet.getType().getId());
            if (resolved != null) {
                pet.setType(resolved);
            }
        }
        petRepository.save(pet);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "pets", key = "#pet.id"),
            @CacheEvict(value = "pets", key = "'all'") })
    public void delete(Pet pet) throws DataAccessException {
        petRepository.delete(pet);
    }
}
