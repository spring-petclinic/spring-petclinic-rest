package org.springframework.samples.petclinic.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.model.Pet;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitFieldsDto;
import org.springframework.samples.petclinic.rest.model.VisitDto;
import org.springframework.samples.petclinic.service.ClinicService;
import org.springframework.samples.petclinic.service.clinicService.ApplicationTestConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class VisitMapperTests {

    @MockitoBean
    private ClinicService clinicService;

    @Autowired
    private VisitMapper visitMapper;

    private static Visit visit(int id, int petId, String description) {
        Pet pet = new Pet();
        pet.setId(petId);
        Visit visit = new Visit();
        visit.setId(id);
        visit.setDate(LocalDate.of(2013, 1, 1));
        visit.setDescription(description);
        visit.setPet(pet);
        return visit;
    }

    @Test
    void mapsPetIdToPetIdAndBack() {
        VisitDto dto = visitMapper.toVisitDto(visit(2, 8, "rabies shot"));
        assertThat(dto.id()).isEqualTo(2);
        assertThat(dto.petId()).isEqualTo(8);
        assertThat(dto.description()).isEqualTo("rabies shot");
        assertThat(dto.date()).isEqualTo(LocalDate.of(2013, 1, 1));

        Visit visit = visitMapper.toVisit(dto);
        assertThat(visit.getId()).isEqualTo(2);
        assertThat(visit.getPet().getId()).isEqualTo(8);
        assertThat(visit.getDescription()).isEqualTo("rabies shot");
        assertThat(visit.getDate()).isEqualTo(LocalDate.of(2013, 1, 1));
    }

    @Test
    void mapsVisitFieldsIgnoringIdAndPet() {
        VisitFieldsDto fields = new VisitFieldsDto("neutered").date(LocalDate.of(2020, 5, 5));
        Visit visit = visitMapper.toVisit(fields);
        assertThat(visit.getId()).isNull();
        assertThat(visit.getPet()).isNull();
        assertThat(visit.getDescription()).isEqualTo("neutered");
        assertThat(visit.getDate()).isEqualTo(LocalDate.of(2020, 5, 5));
    }

    @Test
    void mapsCollection() {
        Collection<VisitDto> dtos = visitMapper.toVisitsDto(List.of(visit(1, 8, "a"), visit(2, 8, "b")));
        assertThat(dtos).extracting(VisitDto::description).containsExactly("a", "b");
        assertThat(visitMapper.toVisitsDto(List.of())).isEmpty();
    }

    @Test
    void nullInputsMapToNull() {
        assertThat(visitMapper.toVisitDto(null)).isNull();
        assertThat(visitMapper.toVisit((VisitDto) null)).isNull();
        assertThat(visitMapper.toVisit((VisitFieldsDto) null)).isNull();
        assertThat(visitMapper.toVisitsDto(null)).isNull();
    }

    @Test
    void visitWithNullDescriptionFailsToMap() {
        Visit visit = visit(2, 8, null);
        assertThatThrownBy(() -> visitMapper.toVisitDto(visit)).isInstanceOf(IllegalArgumentException.class);
    }
}
