package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.repository.OwnerRepository;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"postgres","spring-data-jpa"})
class SmokeContextTest {

    @Autowired(required = false)
    private OwnerRepository ownerRepository;

    @Test
    void shouldLoadOwnerRepository() {
        assertThat(ownerRepository).isNotNull();
    }
}
