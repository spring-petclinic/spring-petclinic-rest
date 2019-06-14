/*
 * Copyright 2016-2017 the original author or authors.
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

package org.springframework.samples.petclinic.repository.jpa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.PetTypeRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Vitaliy Fedoriv
 *
 */

@Repository
@Profile("jpa")
public class JpaPetTypeRepositoryImpl implements PetTypeRepository {
	
    @PersistenceContext
    private EntityManager em;

	@Override
	public PetType findById(int id) {
		return this.em.find(PetType.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<PetType> findAll() throws DataAccessException {
		return this.em.createQuery("SELECT ptype FROM PetType ptype").getResultList();
	}

	@Override
	public void save(PetType petType) throws DataAccessException {
		if (petType.getId() == null) {
            this.em.persist(petType);
        } else {
            this.em.merge(petType);
        }

	}

	@SuppressWarnings("unchecked")
	@Override
	public void delete(PetType petType) throws DataAccessException {
		this.em.remove(this.em.contains(petType) ? petType : this.em.merge(petType));
		Integer petTypeId = petType.getId();
		
		List<Pet> pets = this.em.createQuery("SELECT pet FROM Pet pet WHERE type_id=" + petTypeId).getResultList();
		for (Pet pet : pets){
			List<Visit> visits = pet.getVisits();
			for (Visit visit : visits){
				this.em.createQuery("DELETE FROM Visit visit WHERE id=" + visit.getId()).executeUpdate();
			}
			this.em.createQuery("DELETE FROM Pet pet WHERE id=" + pet.getId()).executeUpdate();
		}
		this.em.createQuery("DELETE FROM PetType pettype WHERE id=" + petTypeId).executeUpdate();
	}

}
