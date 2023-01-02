package org.springframework.samples.petclinic.repository.springdatajpa;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Vet;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;

@Profile("spring-data-jpa")
public class SpringDataVetRepositoryImpl implements VetRepositoryOverride {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Vet> getVetByKeywords(String keyword) {
        if (keyword.isEmpty()) {
            return Collections.emptyList();
        }
        SearchSession searchSession = Search.session(entityManager);
        List<Vet> vets;
        vets = new ArrayList<>(searchSession.search( Vet.class )
            .where( f -> f.nested().objectField( "specialties" ).nest( f.bool()
                .must( f.simpleQueryString().field( "specialties.name" ).matching( keyword ))))
            .fetchHits( 20 ));
        vets.addAll(searchSession.search(Vet.class)
            .where(f -> f.simpleQueryString().fields("lastName", "firstName").matching(keyword))
            .fetchAllHits());

        return vets.stream().distinct().collect(Collectors.toList());

    }
}
