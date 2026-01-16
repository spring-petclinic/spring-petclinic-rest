package org.springframework.samples.petclinic.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.samples.petclinic.messaging.event.PetTypeWriteEvent;
import org.springframework.samples.petclinic.messaging.event.PetTypeWriteOperation;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class PetTypeWriteEventConsumer {

    private final ClinicService clinicService;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PetTypeWriteEventConsumer(
            ClinicService clinicService,
            CacheManager cacheManager
    ) {
        this.clinicService = clinicService;
        this.cacheManager = cacheManager;
    }

    @KafkaListener(
            topics = "petclinic-write-events",
            groupId = "petclinic-write-group"
    )
    @Transactional
    public void consume(PetTypeWriteEvent event) throws Exception {

        PetTypeWriteOperation operation = event.getOperation();

        switch (operation) {

            case CREATE -> {
                PetType petType =
                        objectMapper.readValue(event.getPayload(), PetType.class);
                clinicService.savePetType(petType);
                updateCaches(petType.getId());
            }

            case UPDATE -> {
                PetType updated =
                        objectMapper.readValue(event.getPayload(), PetType.class);
                updated.setId(event.getPetTypeId());
                clinicService.savePetType(updated);
                updateCaches(event.getPetTypeId());
            }

            case DELETE -> {
                PetType existing =
                        clinicService.findPetTypeById(event.getPetTypeId());
                if (existing != null) {
                    clinicService.deletePetType(existing);
                }
                updateCaches(event.getPetTypeId());
            }
        }
    }

    /**
     * Updates Redis caches AFTER DB commit
     */
    private void updateCaches(Integer petTypeId) {

        Cache petTypesCache = cacheManager.getCache("pettypes");
        Cache petTypeByIdCache = cacheManager.getCache("pettypeById");

        // Refresh full list cache
        if (petTypesCache != null) {
            Collection<PetType> allPetTypes =
                    clinicService.findAllPetTypes();
            petTypesCache.put("all", allPetTypes);
        }

        // Refresh single entity cache
        if (petTypeByIdCache != null && petTypeId != null) {
            PetType petType =
                    clinicService.findPetTypeById(petTypeId);
            if (petType != null) {
                petTypeByIdCache.put(petTypeId, petType);
            } else {
                petTypeByIdCache.evict(petTypeId);
            }
        }
    }
}
