package org.springframework.samples.petclinic.repository.jdbc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class JdbcVisitRepositoryImplTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void visitRepositoryCanBeConstructed() {
        JdbcVisitRepositoryImpl repo =
            new JdbcVisitRepositoryImpl(dataSource);

        assertNotNull(repo);
    }
}
