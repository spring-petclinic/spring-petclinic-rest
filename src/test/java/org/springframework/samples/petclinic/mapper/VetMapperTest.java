package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.model.Specialty;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.rest.dto.VetFieldsDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VetMapperTest {

    @Autowired
    private VetMapper mapper;

    // ✅ DTO → ENTITY
    @Test
    void shouldMapVetDto() {

        SpecialtyDto s = new SpecialtyDto();
        s.setName("radiology");

        VetDto dto = new VetDto();
        dto.setFirstName("James");
        dto.setLastName("Carter");
        dto.setSpecialties(List.of(s));

        Vet vet = mapper.toVet(dto);

        assertThat(vet.getFirstName()).isEqualTo("James");
        assertThat(vet.getSpecialties()).hasSize(1);
    }

    @Test
    void shouldReturnNullWhenVetDtoNull() {
        assertThat(mapper.toVet((VetDto) null)).isNull();
    }

    // ✅ Fields DTO
    @Test
    void shouldMapVetFields() {

        VetFieldsDto dto = new VetFieldsDto();
        dto.setFirstName("Rafael");

        Vet vet = mapper.toVet(dto);

        assertThat(vet.getId()).isNull();
    }

    // ✅ ENTITY → DTO
    @Test
    void shouldMapVetToDto() {

        Specialty s = new Specialty();
        s.setName("dentistry");

        Vet vet = new Vet();
        vet.setFirstName("Helen");
        vet.setSpecialties(List.of(s));

        VetDto dto = mapper.toVetDto(vet);

        assertThat(dto.getSpecialties()).hasSize(1);
    }

    // 🔥 COLLECTION
    @Test
    void shouldMapVetCollection() {

        Vet v1 = new Vet();
        v1.setFirstName("A");

        Vet v2 = new Vet();
        v2.setFirstName("B");

        assertThat(mapper.toVetDtos(List.of(v1, v2))).hasSize(2);
    }

    @Test
    void shouldHandleEmptyCollection() {
        assertThat(mapper.toVetDtos(Collections.emptyList())).isEmpty();
    }

    @Test
    void shouldReturnNullWhenCollectionNull() {
        assertThat(mapper.toVetDtos(null)).isNull();
    }
}
