package org.springframework.samples.petclinic.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.samples.petclinic.model.Visit;
import org.springframework.samples.petclinic.rest.dto.VisitDto;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class VisitMapperTest {

    private final VisitMapper mapper = Mappers.getMapper(VisitMapper.class);

    @Test
    void shouldMapVisit() {

        Visit visit = new Visit();
        visit.setId(10);
        visit.setDescription("checkup");
        visit.setDate(LocalDate.now());

        VisitDto dto = mapper.toVisitDto(visit);

        assertThat(dto.getDescription()).isEqualTo("checkup");
    }

    // 🔥 NULL branch
    @Test
    void shouldHandleNullVisit() {

        VisitDto dto = mapper.toVisitDto(null);

        assertThat(dto).isNull();
    }

    // 🔥 COLLECTION branch
    @Test
    void shouldMapVisitCollection() {

        Visit visit = new Visit();
        visit.setId(2);

        var result = mapper.toVisitsDto(Collections.singleton(visit));

        assertThat(result).hasSize(1);
    }
}
