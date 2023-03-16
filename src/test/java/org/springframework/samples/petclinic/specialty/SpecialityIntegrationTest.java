package org.springframework.samples.petclinic.specialty;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.user.UserSqlFactory;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.sql.init.data-locations=" // Disable database population
)
public class SpecialityIntegrationTest {

    private @Autowired ObjectMapper objectMapper;
    private @Autowired RestTemplate restTemplate;
    private @Autowired JdbcTemplate jdbcTemplate;
    private @LocalServerPort int port;

    @Test
    @DisplayName("I can't add a speciality if I'm not passing credentials")
    void testAddNoCredentials() {
        String requestJson = """
            {
                  "name": "virology"
            }
            """;

        HttpClientErrorException.Unauthorized exception = Assertions.catchThrowableOfType(
            () -> restTemplate.exchange(
                "http://localhost:" + port + "/petclinic/api/specialties",
                HttpMethod.POST,
                new HttpEntity<>(requestJson, headers()),
                Object.class
            ),
            HttpClientErrorException.Unauthorized.class
        );

        Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(401));
    }

    @Test
    @DisplayName("""
        [ Add a specialty ]
        Authorized JSON request = JSON response + valid database state

        Given:  A user admin with role vet admin
        When:   Calling POST /specialities with valid credentials to add speciality "virology"
        Then:   - Rest response status is 201
                - A valid id is assigned with the specialty
                - The database has persisted the new specialty
        """)
    void testAdd() {
        jdbcTemplate.update(UserSqlFactory.insertAdmin());
        jdbcTemplate.update(UserSqlFactory.insertRoleVetAdmin());

        String requestJson = """
            {
              "name": "virology"
            }
            """;

        ResponseEntity<String> exchange = restTemplate.exchange(
            "http://localhost:" + port + "/petclinic/api/specialties",
            HttpMethod.POST,
            new HttpEntity<>(requestJson, headers()),
            String.class
        );

        // The id is zero because hsqldb sequences start at zero instead of 1
        String expectedResponseJson = """
            {
              "id": 0,
              "name": "virology"
            }
            """;

        Assertions.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        Assertions.assertThat(exchange.getBody()).isEqualTo(unPretty(expectedResponseJson));

        jdbcTemplate.query("select * from specialties", resultSet -> {
            Assertions.assertThat(resultSet.getInt("id")).isEqualTo(0);
            Assertions.assertThat(resultSet.getString("name")).isEqualTo("virology");
        });
    }

    @Test
    @DisplayName("I can add the same specialty twice")
    void testAddWithId() {
        jdbcTemplate.update(UserSqlFactory.insertAdmin());
        jdbcTemplate.update(UserSqlFactory.insertRoleVetAdmin());
        jdbcTemplate.update(SpecialitySqlFactory.insertRadiology());

        String requestJson = """
            {
              "name": "radiology"
            }
            """;

        ResponseEntity<String> exchange = restTemplate.exchange(
            "http://localhost:" + port + "/petclinic/api/specialties",
            HttpMethod.POST,
            new HttpEntity<>(requestJson, headers()),
            String.class
        );

        String expectedResponseJson = """
            {
              "id": 2,
              "name": "radiology"
            }
            """;

        Assertions.assertThat(exchange.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        Assertions.assertThat(exchange.getBody()).isEqualTo(unPretty(expectedResponseJson));

        Integer amountOfRowsContainingRadiology = jdbcTemplate.queryForObject(
            "select count(name) from specialties where name = 'radiology'",
            Integer.class
        );
        Assertions.assertThat(amountOfRowsContainingRadiology).isEqualTo(2);
    }

    @AfterEach
    void afterEach() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "specialties", "roles", "users");
        Assertions.assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "specialties")).isEqualTo(0);
        Assertions.assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "users")).isEqualTo(0);
        Assertions.assertThat(JdbcTestUtils.countRowsInTable(jdbcTemplate, "roles")).isEqualTo(0);
    }

    private static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("admin", "admin");
        return headers;
    }

    private String unPretty(String prettyJson) {
        try {
            return objectMapper.readValue(prettyJson, JsonNode.class).toString();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @TestConfiguration
    static class LocalTestConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

    }

}
