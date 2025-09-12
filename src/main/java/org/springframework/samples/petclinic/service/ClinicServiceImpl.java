/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.config.CacheConfig;
import org.springframework.samples.petclinic.model.*;
import org.springframework.samples.petclinic.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Mostly used as a facade for all Petclinic controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@Service
public class ClinicServiceImpl implements ClinicService {

    private final PetRepository petRepository;
    private final VetRepository vetRepository;
    private final OwnerRepository ownerRepository;
    private final VisitRepository visitRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PetTypeRepository petTypeRepository;

    @Autowired
    public ClinicServiceImpl(
        PetRepository petRepository,
        VetRepository vetRepository,
        OwnerRepository ownerRepository,
        VisitRepository visitRepository,
        SpecialtyRepository specialtyRepository,
        PetTypeRepository petTypeRepository) {
        this.petRepository = petRepository;
        this.vetRepository = vetRepository;
        this.ownerRepository = ownerRepository;
        this.visitRepository = visitRepository;
        this.specialtyRepository = specialtyRepository;
        this.petTypeRepository = petTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.PETS_CACHE, key = "'allPets'")
    public Collection<Pet> findAllPets() throws DataAccessException {
        return petRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.PETS_CACHE, key = "#pet.id", condition = "#pet.id != null"),
        @CacheEvict(value = CacheConfig.PETS_CACHE, key = "'allPets'")
    })
    public void deletePet(Pet pet) throws DataAccessException {
        petRepository.delete(pet);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.VISITS_CACHE, key = "#visitId", unless = "#result == null")
    public Visit findVisitById(int visitId) throws DataAccessException {
        return findEntityById(() -> visitRepository.findById(visitId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.VISITS_CACHE, key = "'allVisits'")
    public Collection<Visit> findAllVisits() throws DataAccessException {
        return visitRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.VISITS_CACHE, key = "#visit.id", condition = "#visit.id != null"),
        @CacheEvict(value = CacheConfig.VISITS_CACHE, key = "'allVisits'")
    })
    public void deleteVisit(Visit visit) throws DataAccessException {
        visitRepository.delete(visit);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.VETS_CACHE, key = "#id", unless = "#result == null")
    public Vet findVetById(int id) throws DataAccessException {
        return findEntityById(() -> vetRepository.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.VETS_CACHE, key = "'allVets'")
    public Collection<Vet> findAllVets() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.VETS_CACHE, key = "#vet.id", condition = "#vet.id != null"),
        @CacheEvict(value = CacheConfig.VETS_CACHE, key = "'allVets'")
    })
    public void saveVet(Vet vet) throws DataAccessException {
        vetRepository.save(vet);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.VETS_CACHE, key = "#vet.id", condition = "#vet.id != null"),
        @CacheEvict(value = CacheConfig.VETS_CACHE, key = "'allVets'")
    })
    public void deleteVet(Vet vet) throws DataAccessException {
        vetRepository.delete(vet);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.OWNERS_CACHE, key = "'allOwners'")
    public Collection<Owner> findAllOwners() throws DataAccessException {
        return ownerRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.OWNERS_CACHE, key = "#owner.id", condition = "#owner.id != null"),
        @CacheEvict(value = CacheConfig.OWNERS_CACHE, key = "'allOwners'")
    })
    public void deleteOwner(Owner owner) throws DataAccessException {
        ownerRepository.delete(owner);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.PET_TYPES_CACHE, key = "#petTypeId", unless = "#result == null")
    public PetType findPetTypeById(int petTypeId) {
        return findEntityById(() -> petTypeRepository.findById(petTypeId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.PET_TYPES_CACHE, key = "'allPetTypes'")
    public Collection<PetType> findAllPetTypes() throws DataAccessException {
        return petTypeRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.PET_TYPES_CACHE, key = "#petType.id", condition = "#petType.id != null"),
        @CacheEvict(value = CacheConfig.PET_TYPES_CACHE, key = "'allPetTypes'")
    })
    public void savePetType(PetType petType) throws DataAccessException {
        petTypeRepository.save(petType);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.PET_TYPES_CACHE, key = "#petType.id", condition = "#petType.id != null"),
        @CacheEvict(value = CacheConfig.PET_TYPES_CACHE, key = "'allPetTypes'")
    })
    public void deletePetType(PetType petType) throws DataAccessException {
        petTypeRepository.delete(petType);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.SPECIALTIES_CACHE, key = "#specialtyId", unless = "#result == null")
    public Specialty findSpecialtyById(int specialtyId) {
        return findEntityById(() -> specialtyRepository.findById(specialtyId));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.SPECIALTIES_CACHE, key = "'allSpecialties'")
    public Collection<Specialty> findAllSpecialties() throws DataAccessException {
        return specialtyRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.SPECIALTIES_CACHE, key = "#specialty.id", condition = "#specialty.id != null"),
        @CacheEvict(value = CacheConfig.SPECIALTIES_CACHE, key = "'allSpecialties'")
    })
    public void saveSpecialty(Specialty specialty) throws DataAccessException {
        specialtyRepository.save(specialty);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.SPECIALTIES_CACHE, key = "#specialty.id", condition = "#specialty.id != null"),
        @CacheEvict(value = CacheConfig.SPECIALTIES_CACHE, key = "'allSpecialties'")
    })
    public void deleteSpecialty(Specialty specialty) throws DataAccessException {
        specialtyRepository.delete(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.PET_TYPES_CACHE, key = "'allPetTypes'")
    public Collection<PetType> findPetTypes() throws DataAccessException {
        return petRepository.findPetTypes();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.OWNERS_CACHE, key = "#id", unless = "#result == null")
    public Owner findOwnerById(int id) throws DataAccessException {
        return findEntityById(() -> ownerRepository.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.PETS_CACHE, key = "#id", unless = "#result == null")
    public Pet findPetById(int id) throws DataAccessException {
        return findEntityById(() -> petRepository.findById(id));
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.PETS_CACHE, key = "#pet.id", condition = "#pet.id != null"),
        @CacheEvict(value = CacheConfig.PETS_CACHE, key = "'allPets'")
    })
    public void savePet(Pet pet) throws DataAccessException {
        pet.setType(findPetTypeById(pet.getType().getId()));
        petRepository.save(pet);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.VISITS_CACHE, key = "#visit.id", condition = "#visit.id != null"),
        @CacheEvict(value = CacheConfig.VISITS_CACHE, key = "'allVisits'")
    })
    public void saveVisit(Visit visit) throws DataAccessException {
        visitRepository.save(visit);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.VETS_CACHE, key = "'allVets'")
    public Collection<Vet> findVets() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.OWNERS_CACHE, key = "#owner.id", condition = "#owner.id != null"),
        @CacheEvict(value = CacheConfig.OWNERS_CACHE, key = "'allOwners'")
    })
    public void saveOwner(Owner owner) throws DataAccessException {
        ownerRepository.save(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Owner> findOwnerByLastName(String lastName) throws DataAccessException {
        return ownerRepository.findByLastName(lastName);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Visit> findVisitsByPetId(int petId) {
        return visitRepository.findByPetId(petId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Specialty> findSpecialtiesByNameIn(Set<String> names) {
        return findEntityById(() -> specialtyRepository.findSpecialtiesByNameIn(names));
    }

    private <T> T findEntityById(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (ObjectRetrievalFailureException | EmptyResultDataAccessException e) {
            // Just ignore not found exceptions for Jdbc/Jpa realization
            return null;
        }
    }
}
