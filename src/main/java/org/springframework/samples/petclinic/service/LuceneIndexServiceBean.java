package org.springframework.samples.petclinic.service;


import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;


public class LuceneIndexServiceBean {
    private EntityManager entityManager;

    public LuceneIndexServiceBean(EntityManagerFactory entityManagerFactory) {
        entityManager = entityManagerFactory.createEntityManager();
    }

    public void triggerIndexing() {
        try {
            SearchSession searchSession = Search.session(entityManager);
            MassIndexer indexer = searchSession.massIndexer().threadsToLoadObjects(7);
            indexer.startAndWait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
