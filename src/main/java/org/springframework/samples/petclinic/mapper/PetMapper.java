package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.dto.PetDto;
import org.springframework.samples.petclinic.model.Pet;

import java.util.Collection;

@Mapper
public interface PetMapper {
    PetDto toPetDto(Pet pet);
    Collection<PetDto> toPetsDto(Collection<Pet> pets);
}
