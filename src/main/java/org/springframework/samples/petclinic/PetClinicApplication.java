package org.springframework.samples.petclinic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.config.LuceneIndexConfig;

@SpringBootApplication
@Import(LuceneIndexConfig.class)
public class PetClinicApplication extends SpringBootServletInitializer {

    @Autowired
    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }
}
