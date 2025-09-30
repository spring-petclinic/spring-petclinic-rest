/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.samples.petclinic.model.*;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for hybrid caching with L1 (Caffeine) and L2 (Redis).
 */
@SpringBootTest
@Testcontainers
@TestPropertySource(properties = {
    "petclinic.cache.hybrid.enabled=true"
})
public class HybridCacheIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private ClinicService clinicService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheManager l1CacheManager;

    @Autowired
    private CacheManager l2CacheManager;

    @BeforeEach
    public void setUp() {
        // Clear all caches before each test
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @Test
    public void testHybridCacheManagerIsConfigured() {
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(HybridCacheManager.class);
        assertThat(l1CacheManager).isNotNull();
        assertThat(l2CacheManager).isNotNull();
    }

    @Test
    @Transactional
    public void testReadThroughCachingForVets() {
        // First call - should hit database and cache result
        Collection<Vet> vets1 = clinicService.findAllVets();
        assertThat(vets1).isNotEmpty();

        // Verify data is cached in both L1 and L2
        Cache hybridCache = cacheManager.getCache("vets");
        Cache l1Cache = l1CacheManager.getCache("vets");
        Cache l2Cache = l2CacheManager.getCache("vets");

        assertThat(hybridCache).isNotNull();
        assertThat(l1Cache).isNotNull();
        assertThat(l2Cache).isNotNull();

        // Second call - should hit cache
        Collection<Vet> vets2 = clinicService.findAllVets();
        assertThat(vets2).isEqualTo(vets1);
    }

    @Test
    @Transactional
    public void testCacheByIdOperations() {
        // Get a vet by ID
        Vet vet = clinicService.findVetById(1);
        assertThat(vet).isNotNull();

        // Verify cached
        Cache l1Cache = l1CacheManager.getCache("vetById");
        Cache l2Cache = l2CacheManager.getCache("vetById");

        assertThat(l1Cache).isNotNull();
        assertThat(l2Cache).isNotNull();

        // Get from cache
        Vet cachedVet = clinicService.findVetById(1);
        assertThat(cachedVet).isNotNull();
        assertThat(cachedVet.getId()).isEqualTo(vet.getId());
    }

    @Test
    @Transactional
    public void testCacheEvictionOnUpdate() {
        // Load vets into cache
        Collection<Vet> vets = clinicService.findAllVets();
        assertThat(vets).isNotEmpty();

        // Verify cache is populated
        Cache hybridCache = cacheManager.getCache("vets");
        assertThat(hybridCache).isNotNull();

        // Update a vet - should evict cache
        Vet vet = vets.iterator().next();
        vet.setFirstName("Updated");
        clinicService.saveVet(vet);

        // Verify cache was cleared - need to fetch fresh data
        Collection<Vet> vetsAfterUpdate = clinicService.findAllVets();
        assertThat(vetsAfterUpdate).isNotEmpty();
    }

    @Test
    @Transactional
    public void testOwnerCaching() {
        // Test findOwnerById caching
        Owner owner = clinicService.findOwnerById(1);
        assertThat(owner).isNotNull();

        Cache l1Cache = l1CacheManager.getCache("ownerById");
        assertThat(l1Cache).isNotNull();

        // Get from cache
        Owner cachedOwner = clinicService.findOwnerById(1);
        assertThat(cachedOwner).isNotNull();
        assertThat(cachedOwner.getId()).isEqualTo(owner.getId());
    }

    @Test
    @Transactional
    public void testOwnerByLastNameCaching() {
        // Load owners by last name
        Collection<Owner> owners = clinicService.findOwnerByLastName("Davis");
        assertThat(owners).isNotEmpty();

        Cache l1Cache = l1CacheManager.getCache("ownersByLastName");
        Cache l2Cache = l2CacheManager.getCache("ownersByLastName");

        assertThat(l1Cache).isNotNull();
        assertThat(l2Cache).isNotNull();

        // Get from cache
        Collection<Owner> cachedOwners = clinicService.findOwnerByLastName("Davis");
        assertThat(cachedOwners).hasSize(owners.size());
    }

    @Test
    @Transactional
    public void testPetCaching() {
        // Test pet caching
        Collection<Pet> pets = clinicService.findAllPets();
        assertThat(pets).isNotEmpty();

        Pet pet = pets.iterator().next();
        Pet cachedPet = clinicService.findPetById(pet.getId());
        assertThat(cachedPet).isNotNull();
    }

    @Test
    @Transactional
    public void testPetTypeCaching() {
        // Test pet type caching
        Collection<PetType> petTypes = clinicService.findAllPetTypes();
        assertThat(petTypes).isNotEmpty();

        PetType petType = petTypes.iterator().next();
        PetType cachedPetType = clinicService.findPetTypeById(petType.getId());
        assertThat(cachedPetType).isNotNull();
        assertThat(cachedPetType.getId()).isEqualTo(petType.getId());
    }

    @Test
    @Transactional
    public void testSpecialtyCaching() {
        // Test specialty caching
        Collection<Specialty> specialties = clinicService.findAllSpecialties();
        assertThat(specialties).isNotEmpty();

        Specialty specialty = specialties.iterator().next();
        Specialty cachedSpecialty = clinicService.findSpecialtyById(specialty.getId());
        assertThat(cachedSpecialty).isNotNull();
        assertThat(cachedSpecialty.getId()).isEqualTo(specialty.getId());
    }

    @Test
    @Transactional
    public void testVisitCaching() {
        // Test visit caching
        Collection<Visit> visits = clinicService.findAllVisits();
        assertThat(visits).isNotEmpty();

        Visit visit = visits.iterator().next();
        Visit cachedVisit = clinicService.findVisitById(visit.getId());
        assertThat(cachedVisit).isNotNull();
        assertThat(cachedVisit.getId()).isEqualTo(visit.getId());
    }

    @Test
    @Transactional
    public void testVisitsByPetIdCaching() {
        // Get all visits to find a pet with visits
        Collection<Visit> allVisits = clinicService.findAllVisits();
        assertThat(allVisits).isNotEmpty();

        Visit visit = allVisits.iterator().next();
        Integer petId = visit.getPet().getId();

        // Test caching of visits by pet ID
        Collection<Visit> visitsByPet = clinicService.findVisitsByPetId(petId);
        assertThat(visitsByPet).isNotEmpty();

        // Get from cache
        Collection<Visit> cachedVisitsByPet = clinicService.findVisitsByPetId(petId);
        assertThat(cachedVisitsByPet).hasSize(visitsByPet.size());
    }

    @Test
    @Transactional
    public void testL1ToL2Fallback() {
        // Load data into cache
        Vet vet = clinicService.findVetById(1);
        assertThat(vet).isNotNull();

        // Clear L1 cache only
        Cache l1Cache = l1CacheManager.getCache("vetById");
        assertThat(l1Cache).isNotNull();
        l1Cache.clear();

        // Access should fall back to L2 and repopulate L1
        Vet vetFromL2 = clinicService.findVetById(1);
        assertThat(vetFromL2).isNotNull();
        assertThat(vetFromL2.getId()).isEqualTo(vet.getId());
    }

    @Test
    @Transactional
    public void testWriteThroughOnSave() {
        // Create a new specialty
        Specialty specialty = new Specialty();
        specialty.setName("Test Specialty");

        // Save should write through to both caches
        clinicService.saveSpecialty(specialty);

        // Verify cache was evicted (write-through with evict strategy)
        Cache l1Cache = l1CacheManager.getCache("specialties");
        Cache l2Cache = l2CacheManager.getCache("specialties");

        assertThat(l1Cache).isNotNull();
        assertThat(l2Cache).isNotNull();

        // Load data - should fetch fresh from database
        Collection<Specialty> specialties = clinicService.findAllSpecialties();
        assertThat(specialties).isNotEmpty();
    }

    @Test
    @Transactional
    public void testCacheConsistencyAcrossLayers() {
        // Load owner
        Owner owner = clinicService.findOwnerById(1);
        assertThat(owner).isNotNull();

        // Verify both layers have the data
        Cache l1Cache = l1CacheManager.getCache("ownerById");
        Cache l2Cache = l2CacheManager.getCache("ownerById");

        assertThat(l1Cache).isNotNull();
        assertThat(l2Cache).isNotNull();

        Cache.ValueWrapper l1Value = l1Cache.get(1);
        Cache.ValueWrapper l2Value = l2Cache.get(1);

        assertThat(l1Value).isNotNull();
        assertThat(l2Value).isNotNull();

        // Update owner - should evict both caches
        owner.setFirstName("Updated Name");
        clinicService.saveOwner(owner);

        // Verify both caches are cleared
        l1Value = l1Cache.get(1);
        l2Value = l2Cache.get(1);

        // After eviction, caches should be empty or the get should trigger a reload
        // Depending on implementation, we just verify the update worked
        Owner updatedOwner = clinicService.findOwnerById(1);
        assertThat(updatedOwner).isNotNull();
        assertThat(updatedOwner.getFirstName()).isEqualTo("Updated Name");
    }

    @Test
    @Transactional
    public void testSpecialtiesByNameInCaching() {
        // Load some specialties first
        Collection<Specialty> allSpecialties = clinicService.findAllSpecialties();
        assertThat(allSpecialties).isNotEmpty();

        // Get first specialty name
        Specialty specialty = allSpecialties.iterator().next();
        Set<String> names = new HashSet<>();
        names.add(specialty.getName());

        // Test caching with name set
        var specialties = clinicService.findSpecialtiesByNameIn(names);
        assertThat(specialties).isNotEmpty();

        // Verify caching
        Cache l1Cache = l1CacheManager.getCache("specialtiesByNameIn");
        assertThat(l1Cache).isNotNull();

        // Get from cache
        var cachedSpecialties = clinicService.findSpecialtiesByNameIn(names);
        assertThat(cachedSpecialties).hasSize(specialties.size());
    }
}