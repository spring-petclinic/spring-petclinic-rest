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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.repository.VisitRepository;
import org.springframework.stereotype.Repository;

/**
 * A simple JDBC-based implementation of the {@link VisitRepository} interface.
 *
 * @author Ken Krebs
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Sam Brannen
 * @author Thomas Risberg
 * @author Mark Fisher
 * @author Michael Isvy
 * @author Vitaliy Fedoriv
 */
@Repository
@Profile("jdbc")
public class JdbcVisitRepositoryImpl implements VisitRepository {

    protected SimpleJdbcInsert insertVisit;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcVisitRepositoryImpl(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

        this.insertVisit = new SimpleJdbcInsert(dataSource)
            .withTableName("visits")
            .usingGeneratedKeyColumns("id");
    }


    /**
     * Creates a {@link MapSqlParameterSource} based on data values from the supplied {@link Visit} instance.
     */
    protected MapSqlParameterSource createVisitParameterSource(Visit visit) {
        return new MapSqlParameterSource()
            .addValue("id", visit.getId())
            .addValue("visit_date", visit.getDate())
            .addValue("description", visit.getDescription())
            .addValue("pet_id", visit.getPet().getId());
    }

    @Override
    public List<Visit> findByPetId(Integer petId) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", petId);
        JdbcPet pet = this.namedParameterJdbcTemplate.queryForObject(
            "SELECT id as pets_id, name, birth_date, type_id, owner_id FROM pets WHERE id=:id",
            params,
            new JdbcPetRowMapper());

        List<Visit> visits = this.namedParameterJdbcTemplate.query(
            "SELECT id as visit_id, visit_date, description FROM visits WHERE pet_id=:id",
            params, new JdbcVisitRowMapper());

        for (Visit visit : visits) {
            visit.setPet(pet);
        }

        return visits;
    }

    @Override
    public Visit findById(int id) throws DataAccessException {
        Visit visit;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", id);
            visit = this.namedParameterJdbcTemplate.queryForObject(
                "SELECT id as visit_id, visits.pet_id as pets_id, visit_date, description FROM visits WHERE id= :id",
                params,
                new JdbcVisitRowMapperExt());
        } catch (EmptyResultDataAccessException ex) {
            throw new ObjectRetrievalFailureException(Visit.class, id);
        }
        return visit;
    }

    @Override
    public Collection<Visit> findAll() throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        return this.namedParameterJdbcTemplate.query(
            "SELECT visits.id as visit_id, pets.id as pets_id, visit_date, description FROM visits LEFT JOIN pets ON visits.pet_id = pets.id",
            params, new JdbcVisitRowMapperExt());
    }

    @Override
    public void save(Visit visit) throws DataAccessException {
        if (visit.isNew()) {
            Number newKey = this.insertVisit.executeAndReturnKey(createVisitParameterSource(visit));
            visit.setId(newKey.intValue());
        } else {
            this.namedParameterJdbcTemplate.update(
                "UPDATE visits SET visit_date=:visit_date, description=:description, pet_id=:pet_id WHERE id=:id ",
                createVisitParameterSource(visit));
        }
    }

    @Override
    public void delete(Visit visit) throws DataAccessException {
        Map<String, Object> params = new HashMap<>();
        params.put("id", visit.getId());
        this.namedParameterJdbcTemplate.update("DELETE FROM visits WHERE id=:id", params);
    }

    protected class JdbcVisitRowMapperExt implements RowMapper<Visit> {

        @Override
        public Visit mapRow(ResultSet rs, int rowNum) throws SQLException {
            Visit visit = new Visit();
            JdbcPet pet = new JdbcPet();
            PetType petType = new PetType();
            Owner owner = new Owner();
            visit.setId(rs.getInt("visit_id"));
            Date visitDate = rs.getDate("visit_date");
            visit.setDate(new java.sql.Date(visitDate.getTime()).toLocalDate());
            visit.setDescription(rs.getString("description"));
            Map<String, Object> params = new HashMap<>();
            params.put("id", rs.getInt("pets_id"));
            pet = JdbcVisitRepositoryImpl.this.namedParameterJdbcTemplate.queryForObject(
                "SELECT pets.id as pets_id, name, birth_date, type_id, owner_id FROM pets WHERE pets.id=:id",
                params,
                new JdbcPetRowMapper());
            params.put("type_id", pet.getTypeId());
            petType = JdbcVisitRepositoryImpl.this.namedParameterJdbcTemplate.queryForObject(
                "SELECT id, name FROM types WHERE id= :type_id",
                params,
                BeanPropertyRowMapper.newInstance(PetType.class));
            pet.setType(petType);
            params.put("owner_id", pet.getOwnerId());
            owner = JdbcVisitRepositoryImpl.this.namedParameterJdbcTemplate.queryForObject(
                "SELECT id, first_name, last_name, address, city, telephone FROM owners WHERE id= :owner_id",
                params,
                BeanPropertyRowMapper.newInstance(Owner.class));
            pet.setOwner(owner);
            visit.setPet(pet);
            return visit;
        }
    }

}
