package org.springframework.samples.petclinic.service.vet;

import java.util.Collection;

import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.samples.petclinic.service.support.EntityFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class VetServiceImpl implements VetService {

    private final VetRepository vetRepository;

    public VetServiceImpl(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    @Override
    public Vet findById(int id) throws DataAccessException {
        return EntityFinder.findOrNull(() -> vetRepository.findById(id));
    }

    @Override
    public Collection<Vet> findAll() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Vet vet) throws DataAccessException {
        vetRepository.save(vet);
    }

    @Override
    @Transactional
    public void delete(Vet vet) throws DataAccessException {
        vetRepository.delete(vet);
    }
}
