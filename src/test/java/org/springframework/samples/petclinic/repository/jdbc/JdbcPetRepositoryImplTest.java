package org.springframework.samples.petclinic.repository.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.samples.petclinic.repository.VisitRepository;

@SpringBootTest
class JdbcPetRepositoryImplTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Test
    void petRepositoryCanBeConstructed() {
        JdbcPetRepositoryImpl repo =
            new JdbcPetRepositoryImpl(
                dataSource,
                ownerRepository,
                visitRepository
            );

        assertNotNull(repo);
    }
}
