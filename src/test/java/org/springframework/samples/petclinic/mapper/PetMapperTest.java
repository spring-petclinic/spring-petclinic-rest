package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.PetFieldsDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class PetMapperTest {

    private PetMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(PetMapper.class);
    }

    // ✅ ENTITY → DTO
    @Test
    void shouldMapPetToDto() {

        Pet pet = createPet(1, "Fido");
        pet.setOwner(createOwner(99));

        PetDto dto = mapper.toPetDto(pet);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Fido");
        assertThat(dto.getOwnerId()).isEqualTo(99);
    }

    // 🔥 NULL BRANCH
    @Test
    void shouldReturnNullWhenPetNull() {

        PetDto dto = mapper.toPetDto(null);

        assertThat(dto).isNull();
    }

    // 🔥 OWNER NULL BRANCH
    @Test
    void shouldHandleNullOwner() {

        Pet pet = createPet(2, "Kitty");

        PetDto dto = mapper.toPetDto(pet);

        assertThat(dto.getOwnerId()).isNull();
    }

    // 🔥 COLLECTION BRANCH
    @Test
    void shouldMapPetCollection() {

        Pet p1 = createPet(1, "A");
        Pet p2 = createPet(2, "B");

        Collection<PetDto> result = mapper.toPetsDto(List.of(p1, p2));

        assertThat(result).hasSize(2);
    }

    // 🔥 EMPTY COLLECTION
    @Test
    void shouldMapEmptyCollection() {

        Collection<PetDto> result = mapper.toPetsDto(Collections.emptyList());

        assertThat(result).isEmpty();
    }

    // 🔥 NULL COLLECTION
    @Test
    void shouldReturnNullWhenCollectionNull() {

        Collection<PetDto> result = mapper.toPetsDto(null);

        assertThat(result).isNull();
    }

    // ✅ DTO → ENTITY
    @Test
    void shouldMapDtoToPet() {

        PetDto dto = new PetDto();
        dto.setId(5);
        dto.setName("Buddy");
        dto.setBirthDate(LocalDate.now());
        dto.setOwnerId(44);

        Pet pet = mapper.toPet(dto);

        assertThat(pet.getId()).isEqualTo(5);
        assertThat(pet.getOwner()).isNotNull();
        assertThat(pet.getOwner().getId()).isEqualTo(44);
    }

    // 🔥 NULL DTO
    @Test
    void shouldReturnNullWhenDtoNull() {

        Pet pet = mapper.toPet((PetDto) null);

        assertThat(pet).isNull();
    }

    // ✅ Fields DTO → ENTITY
    @Test
    void shouldMapFieldsDto() {

        PetFieldsDto dto = new PetFieldsDto();
        dto.setName("Max");
        dto.setBirthDate(LocalDate.of(2023, 3, 10));

        Pet pet = mapper.toPet(dto);

        assertThat(pet.getName()).isEqualTo("Max");
        assertThat(pet.getId()).isNull();
        assertThat(pet.getOwner()).isNull();

        // ⭐ IMPORTANT — collections are initialized
        assertThat(pet.getVisits()).isNotNull().isEmpty();
    }

    // 🔥 NULL Fields DTO
    @Test
    void shouldReturnNullWhenFieldsNull() {

        Pet pet = mapper.toPet((PetFieldsDto) null);

        assertThat(pet).isNull();
    }

    // ✅ PetType mapping
    @Test
    void shouldMapPetType() {

        PetType type = new PetType();
        type.setId(3);
        type.setName("dog");

        PetTypeDto dto = mapper.toPetTypeDto(type);

        assertThat(dto.getId()).isEqualTo(3);
        assertThat(dto.getName()).isEqualTo("dog");
    }

    // 🔥 COLLECTION branch
    @Test
    void shouldMapPetTypeCollection() {

        PetType t1 = new PetType();
        t1.setName("cat");

        PetType t2 = new PetType();
        t2.setName("bird");

        Collection<PetTypeDto> result = mapper.toPetTypeDtos(List.of(t1, t2));

        assertThat(result).hasSize(2);
    }

    // ---------- helpers ----------

    private Pet createPet(int id, String name) {

        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setBirthDate(LocalDate.now());
        pet.setType(new PetType());

        // ✅ MUST MATCH ENTITY TYPE
        pet.setVisits(new ArrayList<Visit>());

        return pet;
    }

    private Owner createOwner(int id) {

        Owner owner = new Owner();
        owner.setId(id);
        return owner;
    }
}
