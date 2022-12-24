package org.springframework.samples.petclinic.repository.springdatajpa;

import org.springframework.context.annotation.Profile;
import org.springframework.samples.petclinic.model.Owner;

import java.util.List;

@Profile("spring-data-jpa")
public interface OwnerRepositoryOverride {
    List<Owner> getByKeywords(String lastName);
}
