package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.config.LuceneIndexConfig;

@Import(LuceneIndexConfig.class)
@SpringBootApplication
public class PetClinicApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }
}
