package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.springframework.samples.petclinic.dto.PetTypeDto;
import org.springframework.samples.petclinic.model.PetType;

import java.util.Collection;

@Mapper
public interface PetTypeMapper {

    PetType toPetType(PetTypeDto petTypeDto);

    PetTypeDto toPetTypeDto(PetType petType);

    Collection<PetTypeDto> toPetTypeDtos(Collection<PetType> petTypes);
}
