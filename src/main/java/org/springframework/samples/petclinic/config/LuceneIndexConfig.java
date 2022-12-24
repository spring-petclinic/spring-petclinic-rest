package org.springframework.samples.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.samples.petclinic.service.LuceneIndexServiceBean;

import javax.persistence.EntityManagerFactory;

public class LuceneIndexConfig {
    @Bean
    public LuceneIndexServiceBean luceneIndexServiceBean(EntityManagerFactory EntityManagerFactory){
        LuceneIndexServiceBean luceneIndexServiceBean = new LuceneIndexServiceBean(EntityManagerFactory);
        luceneIndexServiceBean.triggerIndexing();
        return luceneIndexServiceBean;
    }

}
