package org.springframework.samples.petclinic.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.samples.petclinic.messaging.event.PetTypeWriteEvent;
import org.springframework.samples.petclinic.messaging.event.PetTypeWriteOperation;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PetTypeWriteEventConsumer {

    private final ClinicService clinicService;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    public PetTypeWriteEventConsumer(
            ClinicService clinicService,
            CacheManager cacheManager
    ) {
        this.clinicService = clinicService;
        this.cacheManager = cacheManager;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(
        topics = "petclinic-write-events",
        groupId = "petclinic-write-group"
    )
    @Transactional
    public void consume(PetTypeWriteEvent event) {

        try {
            PetTypeWriteOperation operation = event.getOperation();

            switch (operation) {

                case CREATE -> {
                    PetType petType =
                            objectMapper.readValue(event.getPayload(), PetType.class);
                    clinicService.savePetType(petType);
                }

                case UPDATE -> {
                    PetType existing =
                            clinicService.findPetTypeById(event.getPetTypeId());

                    if (existing != null) {
                        PetType updated =
                                objectMapper.readValue(event.getPayload(), PetType.class);
                        existing.setName(updated.getName());
                        clinicService.savePetType(existing);
                    }
                }

                case DELETE -> {
                    PetType existing =
                            clinicService.findPetTypeById(event.getPetTypeId());
                    if (existing != null) {
                        clinicService.deletePetType(existing);
                    }
                }
            }

            // ✅ Cache eviction AFTER successful DB transaction
            evictCaches(event.getPetTypeId());

        } catch (Exception ex) {
            // Important:
            // - Transaction will roll back
            // - Kafka offset will NOT be committed
            // - Message will be retried
            throw new RuntimeException("Failed to process PetType write event", ex);
        }
    }

    private void evictCaches(Integer petTypeId) {

        Cache petTypesCache = cacheManager.getCache("pettypes");
        if (petTypesCache != null) {
            petTypesCache.clear();
        }

        if (petTypeId != null) {
            Cache petTypeByIdCache = cacheManager.getCache("pettypeById");
            if (petTypeByIdCache != null) {
                petTypeByIdCache.evict(petTypeId);
            }
        }
    }
}
