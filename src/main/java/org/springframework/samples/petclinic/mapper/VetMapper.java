package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.rest.dto.VetDto;
import org.springframework.samples.petclinic.model.Vet;
import org.springframework.samples.petclinic.rest.dto.VetFieldsDto;

import java.util.Collection;

/**
 * Map Vet & VetoDto using mapstruct
 */
@Mapper(uses = SpecialtyMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VetMapper {
    Vet toVet(VetDto vetDto);

    Vet toVet(VetFieldsDto vetFieldsDto);

    VetDto toVetDto(Vet vet);

    Collection<VetDto> toVetDtos(Collection<Vet> vets);
}
