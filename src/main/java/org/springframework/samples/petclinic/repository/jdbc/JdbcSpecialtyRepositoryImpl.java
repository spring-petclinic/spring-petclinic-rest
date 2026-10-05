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

package org.springframework.samples.petclinic.repository.jdbc;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.repository.SpecialtyRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Vitaliy Fedoriv
 *
 */

@DependsOnDatabaseInitialization
@Repository
public class JdbcSpecialtyRepositoryImpl implements SpecialtyRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Specialty> specialtyRowMapper = (rs, rowNum) -> {
        Specialty specialty = new Specialty();
        specialty.setId(rs.getInt("id"));
        specialty.setName(rs.getString("name"));
        return specialty;
    };

    public JdbcSpecialtyRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Specialty findById(int id) {
        return jdbcTemplate.query("SELECT id, name FROM specialties WHERE id = ?", specialtyRowMapper, id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new ObjectRetrievalFailureException(Specialty.class, id));
    }

    @Override
    public List<Specialty> findSpecialtiesByNameIn(Set<String> names) {
        if (names.isEmpty()) {
            return List.of();
        }
        // one "?" per name: IN (?, ?, ?)
        String placeholders = String.join(", ", Collections.nCopies(names.size(), "?"));
        return jdbcTemplate.query(
            "SELECT id, name FROM specialties WHERE name IN (" + placeholders + ")",
            specialtyRowMapper,
            names.toArray());
    }

    @Override
    public Collection<Specialty> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM specialties", specialtyRowMapper);
    }

    @Override
    public void save(Specialty specialty) {
        if (specialty.isNew()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO specialties (name) VALUES (?)", new String[]{"id"});
                ps.setString(1, specialty.getName());
                return ps;
            }, keyHolder);
            specialty.setId(keyHolder.getKey().intValue());
        } else {
            jdbcTemplate.update("UPDATE specialties SET name = ? WHERE id = ?", specialty.getName(), specialty.getId());
        }
    }

    @Override
    public void delete(Specialty specialty) {
        // links in vet_specialties are removed by ON DELETE CASCADE
        jdbcTemplate.update("DELETE FROM specialties WHERE id = ?", specialty.getId());
    }

}
