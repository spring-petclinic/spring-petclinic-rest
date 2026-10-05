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

import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.repository.PetTypeRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Vitaliy Fedoriv
 *
 */

@DependsOnDatabaseInitialization
@Repository
public class JdbcPetTypeRepositoryImpl implements PetTypeRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<PetType> petTypeRowMapper = (rs, rowNum) -> {
        PetType petType = new PetType();
        petType.setId(rs.getInt("id"));
        petType.setName(rs.getString("name"));
        return petType;
    };

    public JdbcPetTypeRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PetType findById(int id) {
        return jdbcTemplate.query("SELECT id, name FROM types WHERE id = ?", petTypeRowMapper, id)
            .stream()
            .findFirst()
            .orElseThrow(() -> new ObjectRetrievalFailureException(PetType.class, id));
    }

    @Override
    public Collection<PetType> findAll() {
        return jdbcTemplate.query("SELECT id, name FROM types", petTypeRowMapper);
    }

    @Override
    public void save(PetType petType) {
        if (petType.isNew()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO types (name) VALUES (?)", new String[]{"id"});
                ps.setString(1, petType.getName());
                return ps;
            }, keyHolder);
            petType.setId(keyHolder.getKey().intValue());
        } else {
            jdbcTemplate.update("UPDATE types SET name = ? WHERE id = ?", petType.getName(), petType.getId());
        }
    }

    @Override
    public void delete(PetType petType) {
        // pets of this type and their visits are removed by ON DELETE CASCADE
        jdbcTemplate.update("DELETE FROM types WHERE id = ?", petType.getId());
    }

}
