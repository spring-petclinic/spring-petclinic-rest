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

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
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
@DependsOnDatabaseInitialization
@Repository
public class JdbcVisitRepositoryImpl implements VisitRepository {

    /**
     * Each visit is selected together with its pet, the pet's type and the pet's owner.
     */
    private static final String SELECT_VISITS =
        "SELECT v.id, v.visit_date, v.description, " +
            "p.id AS pet_id, p.name AS pet_name, p.birth_date, " +
            "t.id AS type_id, t.name AS type_name, " +
            "o.id AS owner_id, o.first_name, o.last_name, o.address, o.city, o.telephone " +
            "FROM visits v " +
            "JOIN pets p ON v.pet_id = p.id " +
            "JOIN types t ON p.type_id = t.id " +
            "JOIN owners o ON p.owner_id = o.id ";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Visit> visitRowMapper = (rs, rowNum) -> {
        PetType type = new PetType();
        type.setId(rs.getInt("type_id"));
        type.setName(rs.getString("type_name"));

        Owner owner = new Owner();
        owner.setId(rs.getInt("owner_id"));
        owner.setFirstName(rs.getString("first_name"));
        owner.setLastName(rs.getString("last_name"));
        owner.setAddress(rs.getString("address"));
        owner.setCity(rs.getString("city"));
        owner.setTelephone(rs.getString("telephone"));

        Pet pet = new Pet();
        pet.setId(rs.getInt("pet_id"));
        pet.setName(rs.getString("pet_name"));
        pet.setBirthDate(rs.getObject("birth_date", LocalDate.class));
        pet.setType(type);
        pet.setOwner(owner);

        Visit visit = new Visit();
        visit.setId(rs.getInt("id"));
        visit.setDate(rs.getObject("visit_date", LocalDate.class));
        visit.setDescription(rs.getString("description"));
        visit.setPet(pet);
        return visit;
    };

    public JdbcVisitRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Visit> findByPetId(Integer petId) {
        return jdbcTemplate.query(SELECT_VISITS + "WHERE v.pet_id = ?", visitRowMapper, petId);
    }

    @Override
    public Visit findById(int id) {
        return jdbcTemplate.query(SELECT_VISITS + "WHERE v.id = ?", visitRowMapper, id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new ObjectRetrievalFailureException(Visit.class, id));
    }

    @Override
    public Collection<Visit> findAll() {
        return jdbcTemplate.query(SELECT_VISITS, visitRowMapper);
    }

    @Override
    public void save(Visit visit) {
        if (visit.isNew()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO visits (pet_id, visit_date, description) VALUES (?, ?, ?)", new String[]{"id"});
                ps.setInt(1, visit.getPet().getId());
                ps.setObject(2, visit.getDate());
                ps.setString(3, visit.getDescription());
                return ps;
            }, keyHolder);
            visit.setId(keyHolder.getKey().intValue());
        } else {
            jdbcTemplate.update("UPDATE visits SET visit_date = ?, description = ?, pet_id = ? WHERE id = ?",
                visit.getDate(), visit.getDescription(), visit.getPet().getId(), visit.getId());
        }
    }

    @Override
    public void delete(Visit visit) {
        jdbcTemplate.update("DELETE FROM visits WHERE id = ?", visit.getId());
    }

}
