/*
 * Copyright 2002-2018 the original author or authors.
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
import java.util.Collection;
import java.util.List;

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.repository.VetRepository;
import org.springframework.stereotype.Repository;

/**
 * A simple JDBC-based implementation of the {@link VetRepository} interface.
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
public class JdbcVetRepositoryImpl implements VetRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Vet> vetRowMapper = (rs, rowNum) -> {
        Vet vet = new Vet();
        vet.setId(rs.getInt("id"));
        vet.setFirstName(rs.getString("first_name"));
        vet.setLastName(rs.getString("last_name"));
        return vet;
    };

    private final RowMapper<Specialty> specialtyRowMapper = (rs, rowNum) -> {
        Specialty specialty = new Specialty();
        specialty.setId(rs.getInt("id"));
        specialty.setName(rs.getString("name"));
        return specialty;
    };

    public JdbcVetRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Vet> findAll() {
        List<Vet> vets = jdbcTemplate.query(
            "SELECT id, first_name, last_name FROM vets ORDER BY last_name, first_name", vetRowMapper);
        for (Vet vet : vets) {
            loadSpecialties(vet);
        }
        return vets;
    }

    @Override
    public Vet findById(int id) {
        Vet vet = jdbcTemplate.query("SELECT id, first_name, last_name FROM vets WHERE id = ?", vetRowMapper, id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new ObjectRetrievalFailureException(Vet.class, id));
        loadSpecialties(vet);
        return vet;
    }

    private void loadSpecialties(Vet vet) {
        List<Specialty> specialties = jdbcTemplate.query(
            "SELECT s.id, s.name FROM specialties s " +
                "JOIN vet_specialties vs ON vs.specialty_id = s.id " +
                "WHERE vs.vet_id = ?",
            specialtyRowMapper,
            vet.getId());
        for (Specialty specialty : specialties) {
            vet.addSpecialty(specialty);
        }
    }

    @Override
    public void save(Vet vet) {
        if (vet.isNew()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO vets (first_name, last_name) VALUES (?, ?)", new String[]{"id"});
                ps.setString(1, vet.getFirstName());
                ps.setString(2, vet.getLastName());
                return ps;
            }, keyHolder);
            vet.setId(keyHolder.getKey().intValue());
        } else {
            jdbcTemplate.update("UPDATE vets SET first_name = ?, last_name = ? WHERE id = ?",
                vet.getFirstName(), vet.getLastName(), vet.getId());
        }
        updateVetSpecialties(vet);
    }

    private void updateVetSpecialties(Vet vet) {
        jdbcTemplate.update("DELETE FROM vet_specialties WHERE vet_id = ?", vet.getId());
        for (Specialty specialty : vet.getSpecialties()) {
            if (specialty.getId() != null) {
                jdbcTemplate.update("INSERT INTO vet_specialties (vet_id, specialty_id) VALUES (?, ?)",
                    vet.getId(), specialty.getId());
            }
        }
    }

    @Override
    public void delete(Vet vet) {
        // links in vet_specialties are removed by ON DELETE CASCADE
        jdbcTemplate.update("DELETE FROM vets WHERE id = ?", vet.getId());
    }

}
