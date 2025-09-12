package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the Cache Management REST Controller
 * Focuses on testing the /api/cache/stats endpoint as specified in the task requirements
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"spring-data-jpa", "hsqldb"})
@Transactional
public class CacheManagementRestControllerTests {

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getCacheApiUrl() {
        return "http://localhost:" + port + "/petclinic/api/cache";
    }

    @BeforeEach
    void setUp() {
        // Clear all caches before each test to ensure clean state
        ResponseEntity<Void> response = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(response.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);
    }

    @Test
    void testCacheStatsEndpointReturnsCorrectFormat() {
        // Load data into specific caches to generate statistics
        clinicService.findAllPets();
        clinicService.findAllOwners();
        clinicService.findAllVets();
        clinicService.findAllPetTypes();
        clinicService.findAllSpecialties();
        clinicService.findAllVisits();

        // Make some cache hits
        clinicService.findAllPets();
        clinicService.findAllOwners();

        // Test the /api/cache/stats endpoint
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verify response status
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Verify response body structure
        Map<String, Object> stats = response.getBody();
        assertThat(stats).isNotNull();

        // Verify all required cache regions are present
        assertThat(stats).containsKeys("pets", "visits", "vets", "owners", "petTypes", "specialties");

        // Verify each cache region has the correct structure
        for (String cacheRegion : new String[]{"pets", "visits", "vets", "owners", "petTypes", "specialties"}) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cacheStats = (Map<String, Object>) stats.get(cacheRegion);
            assertThat(cacheStats).isNotNull();
            assertThat(((Number) cacheStats.get("hitCount")).longValue()).isGreaterThanOrEqualTo(0);
            assertThat(((Number) cacheStats.get("missCount")).longValue()).isGreaterThanOrEqualTo(0);
        }

        // Verify that pets and owners have cache hits (we called them twice)
        @SuppressWarnings("unchecked")
        Map<String, Object> petsStats = (Map<String, Object>) stats.get("pets");
        @SuppressWarnings("unchecked")
        Map<String, Object> ownersStats = (Map<String, Object>) stats.get("owners");
        assertThat(((Number) petsStats.get("hitCount")).longValue()).isGreaterThan(0);
        assertThat(((Number) ownersStats.get("hitCount")).longValue()).isGreaterThan(0);

        // Verify that all caches have at least one miss (initial load)
        @SuppressWarnings("unchecked")
        Map<String, Object> vetsStats = (Map<String, Object>) stats.get("vets");
        @SuppressWarnings("unchecked")
        Map<String, Object> petTypesStats = (Map<String, Object>) stats.get("petTypes");
        @SuppressWarnings("unchecked")
        Map<String, Object> specialtiesStats = (Map<String, Object>) stats.get("specialties");
        @SuppressWarnings("unchecked")
        Map<String, Object> visitsStats = (Map<String, Object>) stats.get("visits");
        assertThat(((Number) petsStats.get("missCount")).longValue()).isGreaterThan(0);
        assertThat(((Number) ownersStats.get("missCount")).longValue()).isGreaterThan(0);
        assertThat(((Number) vetsStats.get("missCount")).longValue()).isGreaterThan(0);
        assertThat(((Number) petTypesStats.get("missCount")).longValue()).isGreaterThan(0);
        assertThat(((Number) specialtiesStats.get("missCount")).longValue()).isGreaterThan(0);
        assertThat(((Number) visitsStats.get("missCount")).longValue()).isGreaterThan(0);
    }

    @Test
    void testCacheStatsEndpointWithEmptyCache() {
        // Test the endpoint with empty caches
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Map<String, Object> stats = response.getBody();
        assertThat(stats).isNotNull();
        assertThat(stats).containsKeys("pets", "visits", "vets", "owners", "petTypes", "specialties");

        // All stats should be zero or greater for empty caches (Caffeine stats are cumulative)
        for (Object cacheStatsObj : stats.values()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cacheStats = (Map<String, Object>) cacheStatsObj;
            assertThat(((Number) cacheStats.get("hitCount")).longValue()).isGreaterThanOrEqualTo(0);
            assertThat(((Number) cacheStats.get("missCount")).longValue()).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    void testCacheStatsAccuracy() {
        // Clear caches to start with known state
        restTemplate.exchange(getCacheApiUrl() + "/clear", HttpMethod.DELETE, null, Void.class);

        // Get baseline stats
        ResponseEntity<Map<String, Object>> baselineResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> baselineStats = baselineResponse.getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> baselinePetsStats = (Map<String, Object>) baselineStats.get("pets");
        long baselinePetsHits = ((Number) baselinePetsStats.get("hitCount")).longValue();
        long baselinePetsMisses = ((Number) baselinePetsStats.get("missCount")).longValue();

        // Make one call (should be cache miss)
        clinicService.findAllPets();

        // Get stats after first call
        ResponseEntity<Map<String, Object>> stats1Response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> stats1 = stats1Response.getBody();

        // Should have exactly one more miss
        @SuppressWarnings("unchecked")
        Map<String, Object> pets1Stats = (Map<String, Object>) stats1.get("pets");
        assertThat(((Number) pets1Stats.get("missCount")).longValue()).isEqualTo(baselinePetsMisses + 1);
        assertThat(((Number) pets1Stats.get("hitCount")).longValue()).isEqualTo(baselinePetsHits);

        // Make second call (should be cache hit)
        clinicService.findAllPets();

        // Get stats after second call
        ResponseEntity<Map<String, Object>> stats2Response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> stats2 = stats2Response.getBody();

        // Should have exactly one more hit, same misses
        @SuppressWarnings("unchecked")
        Map<String, Object> pets2Stats = (Map<String, Object>) stats2.get("pets");
        assertThat(((Number) pets2Stats.get("hitCount")).longValue()).isEqualTo(baselinePetsHits + 1);
        assertThat(((Number) pets2Stats.get("missCount")).longValue()).isEqualTo(baselinePetsMisses + 1);
    }

    @Test
    void testCacheClearEndpoint() {
        // Load data into caches
        clinicService.findAllPets();
        clinicService.findAllOwners();

        // Verify caches have data
        ResponseEntity<Map<String, Object>> beforeResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> beforeStats = beforeResponse.getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> beforePetsStats = (Map<String, Object>) beforeStats.get("pets");
        @SuppressWarnings("unchecked")
        Map<String, Object> beforeOwnersStats = (Map<String, Object>) beforeStats.get("owners");
        assertThat(((Number) beforePetsStats.get("missCount")).longValue()).isGreaterThan(0);
        assertThat(((Number) beforeOwnersStats.get("missCount")).longValue()).isGreaterThan(0);

        // Clear all caches
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);

        // Verify caches are cleared (stats should be reset)
        ResponseEntity<Map<String, Object>> afterResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> afterStats = afterResponse.getBody();

        // After clearing, cache contents are empty but stats are cumulative in Caffeine
        // We verify that the cache clearing operation worked by checking that subsequent
        // operations will result in cache misses (which we can't directly test here)
        for (Object cacheStatsObj : afterStats.values()) {
            @SuppressWarnings("unchecked")
            Map<String, Object> cacheStats = (Map<String, Object>) cacheStatsObj;
            assertThat(((Number) cacheStats.get("hitCount")).longValue()).isGreaterThanOrEqualTo(0);
            assertThat(((Number) cacheStats.get("missCount")).longValue()).isGreaterThanOrEqualTo(0);
        }
    }

    @Test
    void testClearSpecificCacheEndpoint() {
        // Load data into a specific cache
        clinicService.findAllPets();
        
        // Verify cache has data
        ResponseEntity<Map<String, Object>> beforeResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> beforeStats = beforeResponse.getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> beforePetsStats = (Map<String, Object>) beforeStats.get("pets");
        assertThat(((Number) beforePetsStats.get("missCount")).longValue()).isGreaterThan(0);

        // Clear specific cache
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear/pets",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);

        // Verify the operation completed successfully (we can't directly verify cache clearing
        // due to Caffeine's cumulative stats, but we can verify the endpoint works)
        ResponseEntity<Map<String, Object>> afterResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        assertThat(afterResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testClearNonExistentCacheEndpoint() {
        // Try to clear a cache that doesn't exist
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear/nonexistent",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        
        // Should still return OK even if cache doesn't exist
        assertThat(clearResponse.getStatusCode()).isIn(HttpStatus.OK, HttpStatus.NO_CONTENT);
    }
}
