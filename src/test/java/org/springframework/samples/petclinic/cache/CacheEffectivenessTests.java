package org.springframework.samples.petclinic.cache;

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
import org.springframework.samples.petclinic.model.*;
import org.springframework.samples.petclinic.cache.CacheMonitoringService.CacheStatistics;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive integration tests for cache effectiveness and behavior using public REST API.
 *
 * This test suite validates:
 * - Cache hits, misses, eviction, and data consistency through public interfaces
 * - Cache behavior without introspecting implementation details
 * - Performance benchmarks and database query reduction
 * - Edge cases and error scenarios
 *
 * All tests use black-box testing approach through public APIs and are deterministic.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"spring-data-jpa", "hsqldb"})
@Transactional
public class CacheEffectivenessTests {

    private static final int BENCHMARK_ITERATIONS = 50;

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
        // Clear all caches before each test to ensure clean state using public API
        ResponseEntity<Void> response = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testVetCacheEffectiveness() {
        // Initial call - should be cache miss
        Collection<Vet> vets1 = clinicService.findAllVets();
        assertThat(vets1).isNotEmpty();

        // Second call - should be cache hit
        Collection<Vet> vets2 = clinicService.findAllVets();
        assertThat(vets1).isEqualTo(vets2);

        // Check cache statistics using public REST API
        ResponseEntity<Map<String, CacheStatistics>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats = response.getBody();
        assertThat(stats).isNotNull();
        
        CacheStatistics vetStats = stats.get("vets");
        assertThat(vetStats).isNotNull();
        assertThat(vetStats.hitCount).isGreaterThan(0);
        assertThat(vetStats.missCount).isGreaterThan(0);
        assertThat(vetStats.hitRate).isGreaterThan(0);
    }

    @Test
    void testOwnerCacheEffectiveness() {
        // Test findAllOwners caching
        Collection<Owner> owners1 = clinicService.findAllOwners();
        Collection<Owner> owners2 = clinicService.findAllOwners();
        assertThat(owners1).isEqualTo(owners2);

        // Test findOwnerById caching
        if (!owners1.isEmpty()) {
            Owner firstOwner = owners1.iterator().next();
            int ownerId = firstOwner.getId();

            Owner owner1 = clinicService.findOwnerById(ownerId);
            Owner owner2 = clinicService.findOwnerById(ownerId);

            assertThat(owner1).isEqualTo(owner2);
            assertThat(owner1.getId()).isEqualTo(ownerId);
        }

        // Verify cache statistics using public REST API
        ResponseEntity<Map<String, CacheStatistics>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats = response.getBody();
        assertThat(stats).isNotNull();
        
        CacheStatistics ownerStats = stats.get("owners");
        assertThat(ownerStats).isNotNull();
        assertThat(ownerStats.hitCount).isGreaterThan(0);
    }

    @Test
    void testPetTypeCacheEffectiveness() {
        // Test findAllPetTypes caching
        Collection<PetType> petTypes1 = clinicService.findAllPetTypes();
        Collection<PetType> petTypes2 = clinicService.findAllPetTypes();
        assertThat(petTypes1).isEqualTo(petTypes2);

        // Test individual PetType caching
        if (!petTypes1.isEmpty()) {
            PetType firstPetType = petTypes1.iterator().next();
            int petTypeId = firstPetType.getId();

            PetType petType1 = clinicService.findPetTypeById(petTypeId);
            PetType petType2 = clinicService.findPetTypeById(petTypeId);

            assertThat(petType1).isEqualTo(petType2);
        }

        // Verify cache statistics using public REST API
        ResponseEntity<Map<String, CacheStatistics>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats = response.getBody();
        assertThat(stats).isNotNull();
        
        CacheStatistics petTypeStats = stats.get("petTypes");
        assertThat(petTypeStats).isNotNull();
        assertThat(petTypeStats.hitCount).isGreaterThan(0);
    }

    @Test
    void testSpecialtyCacheEffectiveness() {
        Collection<Specialty> specialties1 = clinicService.findAllSpecialties();
        Collection<Specialty> specialties2 = clinicService.findAllSpecialties();
        assertThat(specialties1).isEqualTo(specialties2);

        // Verify cache statistics using public REST API
        ResponseEntity<Map<String, CacheStatistics>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats = response.getBody();
        assertThat(stats).isNotNull();
        
        CacheStatistics specialtyStats = stats.get("specialties");
        assertThat(specialtyStats).isNotNull();
        assertThat(specialtyStats.hitCount).isGreaterThan(0);
    }

    @Test
    void testCacheEvictionOnSave() {
        // Load data into cache
        Collection<Vet> initialVets = clinicService.findAllVets();
        assertThat(initialVets).isNotEmpty();

        // Verify cache has data using public REST API
        ResponseEntity<Long> sizeResponse = restTemplate.exchange(
            getCacheApiUrl() + "/size/vets",
            HttpMethod.GET,
            null,
            Long.class
        );
        assertThat(sizeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sizeResponse.getBody()).isGreaterThan(0L);

        // Create and save a new vet
        Vet newVet = new Vet();
        newVet.setFirstName("Test");
        newVet.setLastName("Veterinarian");

        clinicService.saveVet(newVet);

        // Verify cache was evicted (cache should be cleared)
        // After eviction, calling findAllVets should reload from database
        Collection<Vet> vetsAfterSave = clinicService.findAllVets();
        assertThat(vetsAfterSave).isNotEmpty();

        // Verify the new vet is in the results (indicating fresh data from database)
        boolean containsNewVet = vetsAfterSave.stream()
            .anyMatch(vet -> "Test".equals(vet.getFirstName()) && "Veterinarian".equals(vet.getLastName()));
        assertThat(containsNewVet).isTrue();
    }

    @Test
    void testCacheEvictionOnDelete() {
        // Load owners into cache
        Collection<Owner> owners = clinicService.findAllOwners();
        assertThat(owners).isNotEmpty();

        // Verify cache has data using public REST API
        ResponseEntity<Long> sizeResponse = restTemplate.exchange(
            getCacheApiUrl() + "/size/owners",
            HttpMethod.GET,
            null,
            Long.class
        );
        assertThat(sizeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sizeResponse.getBody()).isGreaterThan(0L);

        // Delete an owner
        Owner ownerToDelete = owners.iterator().next();
        clinicService.deleteOwner(ownerToDelete);

        // Verify cache was evicted and data is fresh
        Collection<Owner> ownersAfterDelete = clinicService.findAllOwners();

        // The deleted owner should not be in the fresh data
        boolean containsDeletedOwner = ownersAfterDelete.stream()
            .anyMatch(owner -> owner.getId().equals(ownerToDelete.getId()));
        assertThat(containsDeletedOwner).isFalse();
    }

    @Test
    void testMultipleCacheManagement() {
        // Load data into multiple caches
        clinicService.findAllVets();
        clinicService.findAllOwners();
        clinicService.findAllPets();
        clinicService.findAllPetTypes();
        clinicService.findAllSpecialties();

        // Verify all caches have data using public REST API
        ResponseEntity<Map<String, CacheStatistics>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats = response.getBody();
        assertThat(stats).isNotNull();
        assertThat(stats).hasSize(6);
        assertThat(stats.get("vets")).isNotNull();
        assertThat(stats.get("owners")).isNotNull();
        assertThat(stats.get("pets")).isNotNull();
        assertThat(stats.get("petTypes")).isNotNull();
        assertThat(stats.get("specialties")).isNotNull();
        assertThat(stats.get("visits")).isNotNull();

        // Clear all caches using public REST API
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Verify caches are empty (estimated size should be 0)
        ResponseEntity<Map<String, CacheStatistics>> statsAfterClear = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(statsAfterClear.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> clearedStats = statsAfterClear.getBody();
        assertThat(clearedStats).isNotNull();
        clearedStats.values().forEach(stat -> assertThat(stat.estimatedSize).isEqualTo(0));
    }

    @Test
    void testCacheConsistencyAfterModification() {
        // Load a pet type into cache
        Collection<PetType> petTypes = clinicService.findAllPetTypes();
        assertThat(petTypes).isNotEmpty();

        PetType originalPetType = petTypes.iterator().next();
        PetType cachedPetType1 = clinicService.findPetTypeById(originalPetType.getId());

        // Create a new pet type to trigger cache eviction
        PetType newPetType = new PetType();
        newPetType.setName("Test Pet Type");
        clinicService.savePetType(newPetType);

        // Retrieve the pet type again - should be fresh data from database
        PetType cachedPetType2 = clinicService.findPetTypeById(originalPetType.getId());

        // Both should have same data (no stale cache issues)
        assertThat(cachedPetType1.getName()).isEqualTo(cachedPetType2.getName());
        assertThat(cachedPetType1.getId()).isEqualTo(cachedPetType2.getId());
    }

    @Test
    void testCacheHitRateImprovement() {
        // Clear cache to start fresh using public REST API
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Get baseline statistics using public REST API
        ResponseEntity<Map<String, CacheStatistics>> baselineResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        assertThat(baselineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> baselineStats = baselineResponse.getBody();
        assertThat(baselineStats).isNotNull();
        
        CacheStatistics baselineVetStats = baselineStats.get("vets");
        long baselineMisses = baselineVetStats.missCount;
        long baselineHits = baselineVetStats.hitCount;

        // First call - cache miss
        clinicService.findAllVets();

        ResponseEntity<Map<String, CacheStatistics>> stats1Response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        assertThat(stats1Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats1 = stats1Response.getBody();
        assertThat(stats1).isNotNull();
        
        CacheStatistics vetStats1 = stats1.get("vets");
        long missesAfterFirstCall = vetStats1.missCount;
        long hitsAfterFirstCall = vetStats1.hitCount;

        // Verify first call resulted in a cache miss
        assertThat(missesAfterFirstCall).isEqualTo(baselineMisses + 1);
        assertThat(hitsAfterFirstCall).isEqualTo(baselineHits); // No new hits

        // Multiple subsequent calls - cache hits
        for (int i = 0; i < 5; i++) {
            clinicService.findAllVets();
        }

        ResponseEntity<Map<String, CacheStatistics>> stats2Response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        assertThat(stats2Response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats2 = stats2Response.getBody();
        assertThat(stats2).isNotNull();
        
        CacheStatistics vetStats2 = stats2.get("vets");

        // Verify hit count increased and miss count stayed the same
        assertThat(vetStats2.hitCount).isEqualTo(baselineHits + 5); // Should have 5 new hits
        assertThat(vetStats2.missCount).isEqualTo(baselineMisses + 1); // Should have only 1 new miss
        
        // Calculate hit rate for this test's operations only
        long newHits = vetStats2.hitCount - baselineHits;
        long newMisses = vetStats2.missCount - baselineMisses;
        double testHitRate = (double) newHits / (newHits + newMisses);
        assertThat(testHitRate).isGreaterThan(0.5); // Should have good hit rate: 5/(5+1) = 0.833
    }

    @Test
    void testClearSpecificCache() {
        // Load data into cache
        clinicService.findAllVets();
        
        // Verify cache has data using public REST API
        ResponseEntity<Long> sizeResponse = restTemplate.exchange(
            getCacheApiUrl() + "/size/vets",
            HttpMethod.GET,
            null,
            Long.class
        );
        assertThat(sizeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sizeResponse.getBody()).isGreaterThan(0L);
        
        // Clear specific cache using public REST API
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear/vets",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify cache is empty using public REST API
        ResponseEntity<Long> sizeAfterClearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/size/vets",
            HttpMethod.GET,
            null,
            Long.class
        );
        assertThat(sizeAfterClearResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sizeAfterClearResponse.getBody()).isEqualTo(0L);
    }

    @Test
    void testClearNonExistentCache() {
        // This should not throw an exception and should return NO_CONTENT
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear/nonExistentCache",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testGetCacheSizeForNonExistentCache() {
        // Should return NOT_FOUND for non-existent cache
        ResponseEntity<Long> sizeResponse = restTemplate.exchange(
            getCacheApiUrl() + "/size/nonExistentCache",
            HttpMethod.GET,
            null,
            Long.class
        );
        assertThat(sizeResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetCacheSizeForExistingCache() {
        // Load data into cache
        clinicService.findAllVets();
        
        // Should return positive size using public REST API
        ResponseEntity<Long> sizeResponse = restTemplate.exchange(
            getCacheApiUrl() + "/size/vets",
            HttpMethod.GET,
            null,
            Long.class
        );
        assertThat(sizeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(sizeResponse.getBody()).isGreaterThan(0L);
    }

    @Test
    void testGetCacheStatisticsEndpoint() {
        // Load some data to have statistics
        clinicService.findAllVets();
        clinicService.findAllOwners();
        
        // Test the statistics endpoint using public REST API
        ResponseEntity<Map<String, CacheStatistics>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats = response.getBody();
        assertThat(stats).isNotNull();
        assertThat(stats).isNotEmpty();
    }

    @Test
    void testGetCacheStatisticsWithEmptyStats() {
        // Clear all caches to have empty statistics
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // This should handle empty stats gracefully
        ResponseEntity<Map<String, CacheStatistics>> response = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> stats = response.getBody();
        assertThat(stats).isNotNull();
        // All caches should have 0 estimated size after clearing
        stats.values().forEach(stat -> assertThat(stat.estimatedSize).isEqualTo(0));
    }

    @Test
    void testCacheStatisticsDataClass() {
        // Test the CacheStatistics data class
        var stats = new CacheMonitoringService.CacheStatistics(10, 5, 0.67, 2, 8);
        
        assertThat(stats.hitCount).isEqualTo(10);
        assertThat(stats.missCount).isEqualTo(5);
        assertThat(stats.hitRate).isEqualTo(0.67);
        assertThat(stats.evictionCount).isEqualTo(2);
        assertThat(stats.estimatedSize).isEqualTo(8);
    }

    // ========== PERFORMANCE BENCHMARK TESTS ==========

    @Test
    void benchmarkVetServicePerformance() {
        // Warm up - first call will be cache miss
        clinicService.findAllVets();

        // Benchmark cached calls
        long startTime = System.currentTimeMillis();
        List<Collection<Vet>> results = new ArrayList<>();

        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            results.add(clinicService.findAllVets());
        }

        long endTime = System.currentTimeMillis();
        long cachedDuration = endTime - startTime;

        // Clear cache and benchmark uncached calls using public REST API
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        startTime = System.currentTimeMillis();
        List<Collection<Vet>> uncachedResults = new ArrayList<>();

        for (int i = 0; i < BENCHMARK_ITERATIONS; i++) {
            // Clear cache before each call using public REST API
            ResponseEntity<Void> clearVetsResponse = restTemplate.exchange(
                getCacheApiUrl() + "/clear/vets",
                HttpMethod.DELETE,
                null,
                Void.class
            );
            assertThat(clearVetsResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            uncachedResults.add(clinicService.findAllVets());
        }

        endTime = System.currentTimeMillis();
        long uncachedDuration = endTime - startTime;

        // Verify results are consistent
        assertThat(results).isNotEmpty();
        assertThat(uncachedResults).isNotEmpty();
        assertThat(results.get(0)).hasSameElementsAs(uncachedResults.get(0));

        // Performance improvement should be significant
        double performanceImprovement = (double) uncachedDuration / cachedDuration;

        System.out.println("=== Vet Service Performance Benchmark ===");
        System.out.println("Cached calls (" + BENCHMARK_ITERATIONS + "x): " + cachedDuration + "ms");
        System.out.println("Uncached calls (" + BENCHMARK_ITERATIONS + "x): " + uncachedDuration + "ms");
        System.out.println("Performance improvement: " + String.format("%.2fx faster", performanceImprovement));
        System.out.println("=========================================");

        // Cache should provide significant performance improvement
        assertThat(performanceImprovement).isGreaterThan(1.5);
        assertThat(cachedDuration).isLessThan(uncachedDuration);
    }

    @Test
    void measureDatabaseQueryReduction() {
        // Clear caches to start fresh using public REST API
        ResponseEntity<Void> clearResponse = restTemplate.exchange(
            getCacheApiUrl() + "/clear",
            HttpMethod.DELETE,
            null,
            Void.class
        );
        assertThat(clearResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Get baseline statistics using public REST API
        ResponseEntity<Map<String, CacheStatistics>> baselineResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        assertThat(baselineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> baselineStats = baselineResponse.getBody();
        assertThat(baselineStats).isNotNull();
        
        long baselineHits = baselineStats.get("vets").hitCount;
        long baselineMisses = baselineStats.get("vets").missCount;

        // Warm up with one call
        clinicService.findAllVets();

        // Multiple subsequent calls - should be cache hits
        for (int i = 0; i < 10; i++) {
            clinicService.findAllVets();
        }

        // Get final stats using public REST API
        ResponseEntity<Map<String, CacheStatistics>> finalResponse = restTemplate.exchange(
            getCacheApiUrl() + "/stats",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, CacheStatistics>>() {}
        );
        assertThat(finalResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, CacheStatistics> finalStats = finalResponse.getBody();
        assertThat(finalStats).isNotNull();
        
        long finalHits = finalStats.get("vets").hitCount;
        long finalMisses = finalStats.get("vets").missCount;

        // Calculate statistics for this test's operations only
        long newHits = finalHits - baselineHits;
        long newMisses = finalMisses - baselineMisses;
        long newRequests = newHits + newMisses;
        double testHitRate = newRequests > 0 ? (double) newHits / newRequests : 0.0;

        System.out.println("=== Database Query Reduction Analysis ===");
        System.out.println("New database queries (misses): " + newMisses);
        System.out.println("New cache hits: " + newHits);
        System.out.println("New requests: " + newRequests);
        System.out.println("Test hit rate: " + String.format("%.1f%%", testHitRate * 100));
        System.out.println("Query reduction: " + String.format("%.1f%%", testHitRate * 100));
        System.out.println("========================================");

        // After warm-up, we should have predictable cache statistics for this test
        assertThat(newRequests).isEqualTo(11); // Should have made exactly 11 requests (1 warm-up + 10 loop)
        assertThat(newMisses).isEqualTo(1); // Should have exactly 1 miss (warm-up call)
        assertThat(newHits).isEqualTo(10); // Should have exactly 10 hits (loop calls)
        assertThat(testHitRate).isGreaterThan(0.9); // Should have >90% hit rate (10/11)
    }

    // ========== CACHE BEHAVIOR TESTS ==========

    @Test
    void shouldReturnNullForNonExistentEntities() {
        // Test that non-existent entities return null consistently
        Vet nonExistentVet1 = clinicService.findVetById(9999);
        Vet nonExistentVet2 = clinicService.findVetById(9999);

        assertThat(nonExistentVet1).isNull();
        assertThat(nonExistentVet2).isNull();
        assertThat(nonExistentVet1).isEqualTo(nonExistentVet2);

        Owner nonExistentOwner1 = clinicService.findOwnerById(9999);
        Owner nonExistentOwner2 = clinicService.findOwnerById(9999);

        assertThat(nonExistentOwner1).isNull();
        assertThat(nonExistentOwner2).isNull();
        assertThat(nonExistentOwner1).isEqualTo(nonExistentOwner2);
    }

    @Test
    void shouldReturnEmptyCollectionForNonExistentData() {
        Collection<Owner> owners1 = clinicService.findOwnerByLastName("NonExistentLastName");
        Collection<Owner> owners2 = clinicService.findOwnerByLastName("NonExistentLastName");

        assertThat(owners1).isNotNull();
        assertThat(owners1).isEmpty();
        assertThat(owners2).isNotNull();
        assertThat(owners2).isEmpty();
        assertThat(owners1).isEqualTo(owners2);

        Collection<Visit> visits1 = clinicService.findVisitsByPetId(9999);
        Collection<Visit> visits2 = clinicService.findVisitsByPetId(9999);

        assertThat(visits1).isNotNull();
        assertThat(visits1).isEmpty();
        assertThat(visits2).isNotNull();
        assertThat(visits2).isEmpty();
        assertThat(visits1).isEqualTo(visits2);
    }

    @Test
    void shouldHandleSpecialCharactersInSearch() {
        // Test with various special characters
        String[] specialNames = {"O'Connor", "Smith-Jones", "Müller", "Ng", ""};

        for (String lastName : specialNames) {
            Collection<Owner> owners1 = clinicService.findOwnerByLastName(lastName);
            Collection<Owner> owners2 = clinicService.findOwnerByLastName(lastName);

            assertThat(owners1).isNotNull();
            assertThat(owners2).isNotNull();
            assertThat(owners1).isEqualTo(owners2);
        }
    }

    @Test
    void shouldHandleEmptySpecialtyNameSet() {
        Set<String> emptySet = new HashSet<>();
        var specialties1 = clinicService.findSpecialtiesByNameIn(emptySet);
        var specialties2 = clinicService.findSpecialtiesByNameIn(emptySet);

        assertThat(specialties1).isNotNull();
        assertThat(specialties2).isNotNull();
        assertThat(specialties1).isEqualTo(specialties2);
    }

    @Test
    void shouldReturnFreshDataAfterModification() {
        // Get initial count
        Collection<Vet> initialVets = clinicService.findAllVets();
        int initialCount = initialVets.size();

        // Create new vet
        Vet newVet = new Vet();
        newVet.setFirstName("TestFirst");
        newVet.setLastName("TestLast");
        clinicService.saveVet(newVet);

        // Verify count increased
        Collection<Vet> vetsAfterSave = clinicService.findAllVets();
        assertThat(vetsAfterSave).hasSize(initialCount + 1);

        // Verify the new vet is present
        boolean containsNewVet = vetsAfterSave.stream()
            .anyMatch(v -> "TestFirst".equals(v.getFirstName()) && "TestLast".equals(v.getLastName()));
        assertThat(containsNewVet).isTrue();
    }

    @Test
    void shouldHandleBoundaryValues() {
        // Test with ID 1 (should exist based on test data)
        Vet vet1 = clinicService.findVetById(1);
        assertThat(vet1).isNotNull();
        assertThat(vet1.getId()).isEqualTo(1);

        // Test edge case IDs
        Vet vetZero = clinicService.findVetById(0);
        Vet vetNegative = clinicService.findVetById(-1);

        assertThat(vetZero).isNull();
        assertThat(vetNegative).isNull();
    }

    @Test
    void shouldHandleConcurrentReads() {
        // Simulate concurrent access by making multiple calls
        Collection<Vet> vets1 = clinicService.findAllVets();
        Vet singleVet1 = clinicService.findVetById(1);
        Collection<Owner> owners1 = clinicService.findAllOwners();
        Collection<Vet> vets2 = clinicService.findAllVets();
        Vet singleVet2 = clinicService.findVetById(1);
        Collection<Owner> owners2 = clinicService.findAllOwners();

        // Results should be consistent
        assertThat(vets1).isEqualTo(vets2);
        assertThat(singleVet1).isEqualTo(singleVet2);
        assertThat(owners1).isEqualTo(owners2);

        // Data integrity checks
        assertThat(vets1).isNotEmpty();
        assertThat(singleVet1).isNotNull();
        assertThat(owners1).isNotEmpty();
    }

    @Test
    void shouldMaintainVisitPetRelationshipIntegrity() {
        // Get all pets to find one with visits
        Collection<Pet> allPets = clinicService.findAllPets();
        assertThat(allPets).isNotEmpty();

        for (Pet pet : allPets) {
            Collection<Visit> visits1 = clinicService.findVisitsByPetId(pet.getId());
            Collection<Visit> visits2 = clinicService.findVisitsByPetId(pet.getId());

            assertThat(visits1).isEqualTo(visits2);

            // If pet has visits, verify each visit references the correct pet
            for (Visit visit : visits1) {
                Visit fetchedVisit = clinicService.findVisitById(visit.getId());
                assertThat(fetchedVisit).isNotNull();
                assertThat(fetchedVisit.getId()).isEqualTo(visit.getId());
            }
        }
    }
}
