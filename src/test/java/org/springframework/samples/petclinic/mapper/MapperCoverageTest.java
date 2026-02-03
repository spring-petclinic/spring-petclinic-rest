package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.samples.petclinic.model.Owner;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.OwnerDto;
import org.springframework.samples.petclinic.rest.dto.PetDto;
import org.springframework.samples.petclinic.rest.dto.VisitDto;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MapperCoverageTest {

    private final OwnerMapper ownerMapper = Mappers.getMapper(OwnerMapper.class);

    private final PetMapper petMapper = Mappers.getMapper(PetMapper.class);

    private final VisitMapper visitMapper = Mappers.getMapper(VisitMapper.class);

    // ✅ NULL BRANCHES (MapStruct generates MANY)
    @Test
    void shouldHandleNulls() {

        assertNull(ownerMapper.toOwnerDto(null));
        assertNull(petMapper.toPetDto(null));
        assertNull(visitMapper.toVisitDto(null));
    }

    // ✅ OWNER → DTO
    @Test
    void shouldMapOwner() {

        Owner owner = new Owner();
        owner.setId(1);
        owner.setFirstName("John");
        owner.setLastName("Doe");

        OwnerDto dto = ownerMapper.toOwnerDto(owner);

        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
    }

    // ✅ PET mapping
    @Test
    void shouldMapPet() {

        Pet pet = new Pet();
        pet.setId(10);
        pet.setName("Leo");
        pet.setBirthDate(LocalDate.now());

        PetDto dto = petMapper.toPetDto(pet);

        assertEquals("Leo", dto.getName());
    }

    // ✅ COLLECTION BRANCH (BIG COVERAGE BOOST)
    @Test
    void shouldMapPetCollection() {

        Pet pet = new Pet();
        pet.setId(2);
        pet.setName("Max");

        var result = petMapper.toPetsDto(List.of(pet));

        assertEquals(1, result.size());
    }

    // ✅ VISIT mapping
    @Test
    void shouldMapVisit() {

        Visit visit = new Visit();
        visit.setId(5);
        visit.setDescription("checkup");
        visit.setDate(LocalDate.now());

        VisitDto dto = visitMapper.toVisitDto(visit);

        assertEquals("checkup", dto.getDescription());
    }
}
