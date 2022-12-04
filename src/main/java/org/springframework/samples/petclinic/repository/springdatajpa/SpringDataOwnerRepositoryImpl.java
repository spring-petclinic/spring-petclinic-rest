package org.springframework.samples.petclinic.repository.springdatajpa;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Owner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Profile("spring-data-jpa")
public class SpringDataOwnerRepositoryImpl implements OwnerRepositoryOverride{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Owner> getByKeywords(String keyword) {
        if (keyword.isEmpty()) {
            return Collections.emptyList();
        }
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(this.entityManager);
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory().buildQueryBuilder().forEntity(Owner.class).get();


        Query query = queryBuilder.simpleQueryString().onFields("city","address","telephone","firstName","lastName").matching(keyword).createQuery();
        return fullTextEntityManager.createFullTextQuery(query, Owner.class).getResultList();
    }
}
