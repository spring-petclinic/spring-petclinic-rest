package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerMapperTest {

    private final OwnerMapper mapper = Mappers.getMapper(OwnerMapper.class);

    @Test
    void shouldMapOwnerToDto() {

        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setCity("NY");
        owner.setTelephone("123");

        OwnerDto dto = mapper.toOwnerDto(owner);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
    }

    // 🔥 HUGE branch coverage
    @Test
    void shouldHandleNullOwner() {

        OwnerDto dto = mapper.toOwnerDto(null);

        assertThat(dto).isNull();
    }

    // 🔥 collection branch
    @Test
    void shouldMapOwnerCollection() {

        Owner owner = new Owner();
        owner.setId(5);

        var result = mapper.toOwnerDtoCollection(Collections.singleton(owner));

        assertThat(result).hasSize(1);
    }
}
