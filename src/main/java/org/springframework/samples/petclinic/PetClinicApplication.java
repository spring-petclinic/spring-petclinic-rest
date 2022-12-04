package org.springframework.samples.petclinic;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.samples.petclinic.service.SearchService;

@SpringBootApplication

public class PetClinicApplication extends SpringBootServletInitializer {

    @Autowired
    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }
}
