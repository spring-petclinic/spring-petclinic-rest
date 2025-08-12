/*
 * Copyright 2002-2015 the original author or authors.
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


import org.springframework.jdbc.core.RowMapper;
import org.springframework.samples.petclinic.model.Visit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * {@link RowMapper} implementation mapping data from a {@link ResultSet} to the corresponding properties
 * of the {@link Visit} class.
 */
class JdbcVisitRowMapper implements RowMapper<Visit> {

    @Override
    public Visit mapRow(ResultSet rs, int row) throws SQLException {
        Visit visit = new Visit();
        visit.setId(rs.getInt("visit_id"));
        visit.setDate(rs.getObject("visit_date", LocalDate.class));
        visit.setDescription(rs.getString("description"));
        return visit;
    }
}
