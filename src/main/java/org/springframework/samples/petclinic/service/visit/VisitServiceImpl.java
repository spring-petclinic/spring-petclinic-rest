package org.springframework.samples.petclinic.service.visit;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({ "jdbc", "jpa", "spring-data-jpa" })
@Transactional(readOnly = true)
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;

    public VisitServiceImpl(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Override
    @Cacheable(value = "visits", key = "'visit: ' + #visitId")
    public Visit findById(int visitId) throws DataAccessException {
        return EntityFinder.findOrNull(() -> visitRepository.findById(visitId));
    }

    @Override
    @Cacheable(value = "visits", key = "'all'")
    public Collection<Visit> findAll() throws DataAccessException {
        return visitRepository.findAll();
    }

    @Override
    @Cacheable(value = "visits", key = "'pet: ' + #petId")
    public Collection<Visit> findByPetId(int petId) {
        return visitRepository.findByPetId(petId);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "visits", key = "'visit: ' + #visit.id"),
            @CacheEvict(value = "visits", key = "'all'"),
            @CacheEvict(value = "visits", key = "'pet: ' + #visit.pet.id")
    })
    public void save(Visit visit) throws DataAccessException {
        visitRepository.save(visit);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "visits", key = "'visit: ' + #visit.id"),
            @CacheEvict(value = "visits", key = "'all'"),
            @CacheEvict(value = "visits", key = "'pet: ' + #visit.pet.id")
    })
    public void delete(Visit visit) throws DataAccessException {
        visitRepository.delete(visit);
    }
}
