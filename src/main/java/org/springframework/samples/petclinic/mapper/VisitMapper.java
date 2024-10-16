package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import java.util.Collection;

/**
 * Map Visit & VisitDto using mapstruct
 */
@Mapper(uses = PetMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VisitMapper {
    Visit toVisit(VisitDto visitDto);

    Visit toVisit(VisitFieldsDto visitFieldsDto);

    @Mapping(source = "pet.id", target = "petId")
    VisitDto toVisitDto(Visit visit);

    Collection<VisitDto> toVisitsDto(Collection<Visit> visits);

}
