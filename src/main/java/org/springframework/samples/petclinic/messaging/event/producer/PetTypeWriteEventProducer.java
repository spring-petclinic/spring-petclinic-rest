package org.springframework.samples.petclinic.messaging.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.samples.petclinic.messaging.event.PetTypeWriteEvent;
import org.springframework.stereotype.Component;

@Component
public class PetTypeWriteEventProducer {

    private static final String TOPIC = "petclinic-write-events";

    private final KafkaTemplate<String, PetTypeWriteEvent> kafkaTemplate;

    public PetTypeWriteEventProducer(
            KafkaTemplate<String, PetTypeWriteEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PetTypeWriteEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
