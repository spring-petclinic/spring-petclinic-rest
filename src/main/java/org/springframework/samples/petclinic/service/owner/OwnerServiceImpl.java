package org.springframework.samples.petclinic.service.owner;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({ "jdbc", "jpa", "spring-data-jpa" })
@Transactional(readOnly = true)
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    @Cacheable(value = "owners", key = "#id")
    public Owner findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> ownerRepository.findById(id));
    }

    @Override
    @Cacheable(value = "owners", key = "'all'")
    public Collection<Owner> findAll() throws DataAccessException {
        return ownerRepository.findAll();
    }

    @Override
    @Cacheable(value = "owners", key = "#lastName")
    public Collection<Owner> findByLastName(String lastName) throws DataAccessException {
        return ownerRepository.findByLastName(lastName);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "owners", key = "#owner.id"),
            @CacheEvict(value = "owners", key = "'all'"),
            @CacheEvict(value = "owners", key = "#owner.lastName") })
    public void save(Owner owner) throws DataAccessException {
        ownerRepository.save(owner);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "owners", key = "#owner.id"),
            @CacheEvict(value = "owners", key = "'all'"),
            @CacheEvict(value = "owners", key = "#owner.lastName") })
    public void delete(Owner owner) throws DataAccessException {
        ownerRepository.delete(owner);
    }
}
