package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import java.util.Collection;

/**
 * Map Visit & VisitDto using mapstruct
 */
@Mapper(uses = {PetMapper.class, VetMapper.class})
public interface VisitMapper {
    @Mapping(source = "vetId", target = "vet.id")
    Visit toVisit(VisitDto visitDto);

    Visit toVisit(VisitFieldsDto visitFieldsDto);

    @Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "vet.id", target = "vetId")
    VisitDto toVisitDto(Visit visit);

    Collection<VisitDto> toVisitsDto(Collection<Visit> visits);

}
