package org.springframework.samples.petclinic.service.vet;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({ "jdbc", "jpa", "spring-data-jpa" })
@Transactional(readOnly = true)
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;

    public VetServiceImpl(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    @Cacheable(value = "vets", key = "#id")
    public Vet findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> vetRepository.findById(id));
    }

    @Override
    @Cacheable(value = "vets", key = "'all'")
    public Collection<Vet> findAll() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "vets", key = "#vet.id"),
            @CacheEvict(value = "vets", key = "'all'")
    })
    public void save(Vet vet) throws DataAccessException {
        vetRepository.save(vet);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "vets", key = "#vet.id"),
            @CacheEvict(value = "vets", key = "'all'")
    })
    public void delete(Vet vet) throws DataAccessException {
        vetRepository.delete(vet);
    }
}
