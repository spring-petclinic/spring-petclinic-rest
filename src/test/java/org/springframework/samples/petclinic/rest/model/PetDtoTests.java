package org.springframework.samples.petclinic.rest.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.validation.ConstraintViolation;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

class PetDtoTests {

    private static final LocalDate BIRTH = LocalDate.of(2010, 9, 7);

    private PetTypeDto type() {
        return new PetTypeDto("cat", 1);
    }

    @Test
    void buildsValidRecord() {
        PetDto pet = new PetDto("Leo", BIRTH, type(), 1, 2, List.of());
        assertThat(pet.name()).isEqualTo("Leo");
        assertThat(pet.birthDate()).isEqualTo(BIRTH);
        assertThat(pet.type().getName()).isEqualTo("cat");
        assertThat(pet.id()).isEqualTo(1);
        assertThat(pet.ownerId()).isEqualTo(2);
    }

    @Test
    void rejectsNullName() {
        assertThatThrownBy(() -> new PetDto(null, BIRTH, type(), 1, 2, List.of()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("name");
    }

    @Test
    void rejectsTooLongName() {
        assertThatThrownBy(() -> new PetDto("x".repeat(31), BIRTH, type(), 1, 2, List.of()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("name");
        assertThat(new PetDto("x".repeat(30), BIRTH, type(), 1, 2, List.of()).name()).hasSize(30);
    }

    @Test
    void allowsEmptyName() {
        assertThat(new PetDto("", BIRTH, type(), 1, 2, List.of()).name()).isEmpty();
    }

    @Test
    void rejectsNullBirthDateAndType() {
        assertThatThrownBy(() -> new PetDto("Leo", null, type(), 1, 2, List.of()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("birthDate");
        assertThatThrownBy(() -> new PetDto("Leo", BIRTH, null, 1, 2, List.of()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("type");
    }

    @Test
    void rejectsNegativeIds() {
        assertThatThrownBy(() -> new PetDto("Leo", BIRTH, type(), -1, 2, List.of()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("id");
        assertThatThrownBy(() -> new PetDto("Leo", BIRTH, type(), 1, -1, List.of()))
            .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ownerId");
    }

    @Test
    void acceptsNullIds() {
        PetDto pet = new PetDto("Leo", BIRTH, type(), null, null, List.of());
        assertThat(pet.id()).isNull();
        assertThat(pet.ownerId()).isNull();
    }

    @Test
    void nullVisitsBecomeEmptyList() {
        assertThat(new PetDto("Leo", BIRTH, type(), 1, 2, null).visits()).isEmpty();
    }

    @Test
    void visitsAreDefensivelyCopiedAndImmutable() {
        List<VisitDto> source = new ArrayList<>();
        source.add(new VisitDto(null, "d", 1, 1));
        PetDto pet = new PetDto("Leo", BIRTH, type(), 1, 2, source);
        source.add(new VisitDto(null, "e", 2, 1));
        assertThat(pet.visits()).hasSize(1);
        assertThatThrownBy(() -> pet.visits().add(new VisitDto(null, "f", 3, 1)))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void futureBirthDateIsReportedByValidator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        PetDto pet = new PetDto("Leo", LocalDate.now().plusDays(5), type(), 1, 2, List.of());
        Set<ConstraintViolation<PetDto>> violations = validator.validate(pet);
        assertThat(violations).extracting(c -> c.getPropertyPath().toString()).contains("birthDate");
        assertThat(validator.validate(new PetDto("Leo", BIRTH, type(), 1, 2, List.of()))).isEmpty();
    }

    @Test
    void jsonRoundTripKeepsPropertyNamesAndDateFormat() {
        JsonMapper mapper = JsonMapper.builder().build();
        PetDto pet = new PetDto("Leo", BIRTH, type(), 1, 2, List.of(new VisitDto(BIRTH, "d", 3, 1)));
        String json = mapper.writeValueAsString(pet);
        assertThat(json).contains("\"name\":\"Leo\"").contains("\"birthDate\":\"2010-09-07\"")
            .contains("\"ownerId\":2").contains("\"visits\":[").contains("\"petId\":1");
        assertThat(mapper.readValue(json, PetDto.class)).isEqualTo(pet);
    }

    @Test
    void jsonWithoutVisitsDeserializesToEmptyList() {
        JsonMapper mapper = JsonMapper.builder().build();
        PetDto pet = mapper.readValue(
            "{\"name\":\"Leo\",\"birthDate\":\"2010-09-07\",\"type\":{\"name\":\"cat\",\"id\":1}}", PetDto.class);
        assertThat(pet.visits()).isEmpty();
    }

    @Test
    void jsonMissingNameFails() {
        JsonMapper mapper = JsonMapper.builder().build();
        assertThatThrownBy(() -> mapper.readValue(
            "{\"birthDate\":\"2010-09-07\",\"type\":{\"name\":\"cat\",\"id\":1}}", PetDto.class))
            .isInstanceOf(JacksonException.class);
    }
}
