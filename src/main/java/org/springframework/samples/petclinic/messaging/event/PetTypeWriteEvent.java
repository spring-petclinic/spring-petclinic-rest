package org.springframework.samples.petclinic.messaging.event;

import java.io.Serializable;

public class PetTypeWriteEvent implements Serializable {

    private PetTypeWriteOperation operation;
    private Integer petTypeId;
    private String payload;

    public PetTypeWriteEvent() {
    }

    public PetTypeWriteEvent(
            PetTypeWriteOperation operation,
            Integer petTypeId,
            String payload
    ) {
        this.operation = operation;
        this.petTypeId = petTypeId;
        this.payload = payload;
    }

    public PetTypeWriteOperation getOperation() {
        return operation;
    }

    public void setOperation(PetTypeWriteOperation operation) {
        this.operation = operation;
    }

    public Integer getPetTypeId() {
        return petTypeId;
    }

    public void setPetTypeId(Integer petTypeId) {
        this.petTypeId = petTypeId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
