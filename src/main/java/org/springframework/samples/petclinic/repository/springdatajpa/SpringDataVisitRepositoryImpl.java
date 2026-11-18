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

package org.springframework.samples.petclinic.repository.springdatajpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Visit;

import java.time.LocalDate;
import java.util.List;
/**
 * @author Vitaliy Fedoriv
 *
 */

@Profile("spring-data-jpa")
public class SpringDataVisitRepositoryImpl implements VisitRepositoryOverride {

	@PersistenceContext
    private EntityManager em;

	@Override
	public void delete(Visit visit) throws DataAccessException {
		String visitId = visit.getId().toString();
		this.em.createQuery("DELETE FROM Visit visit WHERE id=" + visitId).executeUpdate();
        if (em.contains(visit)) {
            em.remove(visit);
        }
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Visit> findByDateRange(LocalDate from, LocalDate to) {
		StringBuilder jpql = new StringBuilder("SELECT v FROM Visit v WHERE 1=1");
		if (from != null) jpql.append(" AND v.date >= :from");
		if (to != null)   jpql.append(" AND v.date <= :to");
		jpql.append(" ORDER BY v.date, v.id");
		jakarta.persistence.Query query = this.em.createQuery(jpql.toString());
		if (from != null) query.setParameter("from", from);
		if (to != null)   query.setParameter("to", to);
		return query.getResultList();
	}

}
