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
package org.springframework.samples.petclinic.repository.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.PetRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.samples.petclinic.util.EntityUtils;
import org.springframework.stereotype.Repository;

/**
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 * @author Vitaliy Fedoriv
 */
@Repository
@Profile("jdbc")
public class JdbcPetRepositoryImpl implements PetRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private SimpleJdbcInsert insertPet;

    private OwnerRepository ownerRepository;

    private VisitRepository visitRepository;


    public JdbcPetRepositoryImpl(DataSource dataSource,
    		OwnerRepository ownerRepository,
    		VisitRepository visitRepository) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.insertPet = new SimpleJdbcInsert(dataSource)
            .withTableName("pets")
            .usingGeneratedKeyColumns("id");

        this.ownerRepository = ownerRepository;
        this.visitRepository = visitRepository;
    }

    @Override
    public List<PetType> findPetTypes() throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        return this.namedParameterJdbcTemplate.query(
            "SELECT id, name FROM types ORDER BY name",
            params,
            BeanPropertyRowMapper.newInstance(PetType.class));
    }

    @Override
    public Pet findById(int id) throws DataAccessException {
        Integer ownerId;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            ownerId = this.namedParameterJdbcTemplate.queryForObject("SELECT owner_id FROM pets WHERE id=:id", params, Integer.class);
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Pet.class, id);
        }
        Owner owner = this.ownerRepository.findById(ownerId);
        return EntityUtils.getById(owner.getPets(), Pet.class, id);
    }

    @Override
    public void save(Pet pet) throws DataAccessException {
        if (pet.isNew()) {
            Number newKey = this.insertPet.executeAndReturnKey(
                createPetParameterSource(pet));
            pet.setId(newKey.intValue());
        } else {
            this.namedParameterJdbcTemplate.update(
                "UPDATE pets SET name=:name, birth_date=:birth_date, type_id=:type_id, " +
                    "owner_id=:owner_id WHERE id=:id",
                createPetParameterSource(pet));
        }
    }

    /**
     * Creates a {@link MapSqlParameterSource} based on data values from the supplied {@link Pet} instance.
     */
    private MapSqlParameterSource createPetParameterSource(Pet pet) {
        return new MapSqlParameterSource()
            .addValue("id", pet.getId())
            .addValue("name", pet.getName())
            .addValue("birth_date", pet.getBirthDate())
            .addValue("type_id", pet.getType().getId())
            .addValue("owner_id", pet.getOwner().getId());
    }
    
	@Override
	public Collection<Pet> findAll() throws DataAccessException {
		Map<String, Object> params = new HashMap<>();
		Collection<Pet> pets = new ArrayList<Pet>();
		Collection<JdbcPet> jdbcPets = new ArrayList<JdbcPet>();
		jdbcPets = this.namedParameterJdbcTemplate
				.query("SELECT pets.id as pets_id, name, birth_date, type_id, owner_id FROM pets",
				params,
				new JdbcPetRowMapper());
		Collection<PetType> petTypes = this.namedParameterJdbcTemplate.query("SELECT id, name FROM types ORDER BY name",
				new HashMap<String,
				Object>(), BeanPropertyRowMapper.newInstance(PetType.class));
		Collection<Owner> owners = this.namedParameterJdbcTemplate.query(
				"SELECT id, first_name, last_name, address, city, telephone FROM owners ORDER BY last_name",
				new HashMap<String, Object>(),
				BeanPropertyRowMapper.newInstance(Owner.class));
		for (JdbcPet jdbcPet : jdbcPets) {
			jdbcPet.setType(EntityUtils.getById(petTypes, PetType.class, jdbcPet.getTypeId()));
			jdbcPet.setOwner(EntityUtils.getById(owners, Owner.class, jdbcPet.getOwnerId()));
			// TODO add visits
			pets.add(jdbcPet);
		}
		return pets;
	}

	@Override
	public void delete(Pet pet) throws DataAccessException {
		Map<String, Object> pet_params = new HashMap<>();
		pet_params.put("id", pet.getId());
		List<Visit> visits = pet.getVisits();
		// cascade delete visits
		for (Visit visit : visits) {
			Map<String, Object> visit_params = new HashMap<>();
			visit_params.put("id", visit.getId());
			this.namedParameterJdbcTemplate.update("DELETE FROM visits WHERE id=:id", visit_params);
		}
		this.namedParameterJdbcTemplate.update("DELETE FROM pets WHERE id=:id", pet_params);
	}

}
