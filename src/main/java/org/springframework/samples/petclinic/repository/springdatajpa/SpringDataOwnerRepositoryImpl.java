package org.springframework.samples.petclinic.repository.springdatajpa;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Owner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Profile("spring-data-jpa")
public class SpringDataOwnerRepositoryImpl implements OwnerRepositoryOverride {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Owner> getByKeywords(String keyword) {
        if (keyword.isEmpty()) {
            return Collections.emptyList();
        }
        SearchSession searchSession = Search.session(entityManager);
        return searchSession.search(Owner.class)
            .where(f -> f.simpleQueryString().fields("lastName", "firstName", "address", "city", "telephone").matching(keyword))
            .fetchAllHits();
    }
}
