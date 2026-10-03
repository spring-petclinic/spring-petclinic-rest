package org.springframework.samples.petclinic.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;

import java.util.Collection;

/**
 * Map Visit & VisitDto using mapstruct
 */
@Mapper(uses = PetMapper.class)
public interface VisitMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "petId", target = "pet.id")
    Visit toVisit(VisitDto visitDto);

    @Mapping(source = "date", target = "date")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    Visit toVisit(VisitFieldsDto visitFieldsDto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "pet.id", target = "petId")
    VisitDto toVisitDto(Visit visit);

    Collection<VisitDto> toVisitsDto(Collection<Visit> visits);

}
