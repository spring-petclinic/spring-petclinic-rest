package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.model.PetType;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.dto.PetTypeFieldsDto;

import java.util.Collection;
import java.util.List;

/**
 * Map PetType & PetTypeDto using mapstruct
 */
@Mapper
public interface PetTypeMapper {

    PetType toPetType(PetTypeDto petTypeDto);

    @Mapping(target = "id", ignore = true)
    PetType toPetType(PetTypeFieldsDto petTypeFieldsDto);

    PetTypeDto toPetTypeDto(PetType petType);
    PetTypeFieldsDto toPetTypeFieldsDto(PetType petType);

    List<PetTypeDto> toPetTypeDtos(Collection<PetType> petTypes);
}
