package org.springframework.samples.petclinic.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.samples.petclinic.rest.dto.SpecialtyDto;
import org.springframework.samples.petclinic.rest.dto.SpecialtyFieldsDto;
import org.springframework.samples.petclinic.model.Specialty;
import java.util.Collection;
/**
 * Map Specialty & SpecialtyDto using mapstruct
 */
@Mapper
public interface SpecialtyMapper {
    @Mapping(target = "id", ignore = true)
    Specialty toSpecialty(SpecialtyDto specialtyDto);
    @Mapping(target = "id", ignore = true)
    Specialty toSpecialty(SpecialtyFieldsDto specialtyFieldsDto);
    SpecialtyDto toSpecialtyDto(Specialty specialty);
    Collection<SpecialtyDto> toSpecialtyDtos(Collection<Specialty> specialties);
    Collection<Specialty> toSpecialtys(Collection<SpecialtyDto> specialties);
}