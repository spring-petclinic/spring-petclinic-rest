
package org.springframework.samples.petclinic.repository.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("jdbc")
class JdbcVetRepositoryCoverageTest {

    @Autowired
    private JdbcVetRepositoryImpl vetRepository;

    @Test
    void shouldExecuteFindAllAndCoverRowMapper() {
        Collection<Vet> vets = vetRepository.findAll();
        assertNotNull(vets);
    }

    @Test
    void shouldThrowExceptionWhenVetNotFound() {
        assertThrows(
            DataAccessException.class,
            () -> vetRepository.findById(99999)
        );
    }
}
