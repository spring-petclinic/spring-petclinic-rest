package org.springframework.samples.petclinic.rest.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

class VisitDtoTests {

    @Test
    void buildsValidRecord() {
        VisitDto visit = new VisitDto(LocalDate.of(2013, 1, 1), "rabies shot", 1, 2);
        assertThat(visit.date()).isEqualTo(LocalDate.of(2013, 1, 1));
        assertThat(visit.description()).isEqualTo("rabies shot");
        assertThat(visit.id()).isEqualTo(1);
        assertThat(visit.petId()).isEqualTo(2);
    }

    @Test
    void acceptsNullIdAndDate() {
        VisitDto visit = new VisitDto(null, "checkup", null, 2);
        assertThat(visit.id()).isNull();
        assertThat(visit.date()).isNull();
    }

    @Test
    void rejectsNullDescription() {
        assertThatThrownBy(() -> new VisitDto(null, null, 1, 2))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("description");
    }

    @Test
    void rejectsEmptyDescription() {
        assertThatThrownBy(() -> new VisitDto(null, "", 1, 2))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("description");
    }

    @Test
    void rejectsTooLongDescription() {
        assertThatThrownBy(() -> new VisitDto(null, "x".repeat(256), 1, 2))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("description");
        assertThat(new VisitDto(null, "x".repeat(255), 1, 2).description()).hasSize(255);
    }

    @Test
    void rejectsNullOrNegativePetId() {
        assertThatThrownBy(() -> new VisitDto(null, "d", 1, null))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("petId");
        assertThatThrownBy(() -> new VisitDto(null, "d", 1, -1))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("petId");
    }

    @Test
    void rejectsNegativeId() {
        assertThatThrownBy(() -> new VisitDto(null, "d", -1, 2))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("id");
    }

    @Test
    void jsonRoundTripKeepsPropertyNamesAndDateFormat() {
        JsonMapper mapper = JsonMapper.builder().build();
        VisitDto visit = new VisitDto(LocalDate.of(2013, 1, 1), "rabies shot", 1, 2);
        String json = mapper.writeValueAsString(visit);
        assertThat(json).contains("\"date\":\"2013-01-01\"").contains("\"description\":\"rabies shot\"")
            .contains("\"id\":1").contains("\"petId\":2");
        assertThat(mapper.readValue(json, VisitDto.class)).isEqualTo(visit);
    }

    @Test
    void jsonMissingDescriptionFails() {
        JsonMapper mapper = JsonMapper.builder().build();
        assertThatThrownBy(() -> mapper.readValue("{\"petId\":1}", VisitDto.class))
            .isInstanceOf(JacksonException.class);
    }
}
