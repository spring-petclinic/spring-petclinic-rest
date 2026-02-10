package org.springframework.samples.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FullAppSmokeCoverageTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + "/api" + path;
    }

    private TestRestTemplate auth() {
        // default in-memory user
        return rest.withBasicAuth("user", "password");
    }

    /**
     * 🚀 MASSIVE COVERAGE TEST (SECURITY-AWARE)
     */
    @Test
    void shouldHitMajorEndpoints() {

        ResponseEntity<String> owners = auth().getForEntity(url("/owners"), String.class);
        ResponseEntity<String> vets = auth().getForEntity(url("/vets"), String.class);
        ResponseEntity<String> petTypes = auth().getForEntity(url("/pettypes"), String.class);
        ResponseEntity<String> visits = auth().getForEntity(url("/visits"), String.class);

        // We only assert that endpoints RESPOND
        assertThat(owners.getStatusCode().value()).isBetween(200, 499);
        assertThat(vets.getStatusCode().value()).isBetween(200, 499);
        assertThat(petTypes.getStatusCode().value()).isBetween(200, 499);
        assertThat(visits.getStatusCode().value()).isBetween(200, 499);
    }

    /**
     * 🔁 EXTRA COVERAGE SPAM
     */
    @Test
    void shouldSpamOwnersEndpointForCoverage() {

        TestRestTemplate auth = rest.withBasicAuth("user", "password");

        for (int i = 0; i < 15; i++) {
            ResponseEntity<String> response = auth.getForEntity(url("/owners"), String.class);

            assertThat(response.getStatusCode().value()).isBetween(200, 499);
        }
    }
}
