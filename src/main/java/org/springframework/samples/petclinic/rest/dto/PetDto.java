package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.rest.validation.PetAgeValidation;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * A pet.
 */
@Schema(name = "Pet", description = "A pet.")
public record PetDto(
    @NotNull
    @Size(max = 30)
    @Schema(name = "name", example = "Leo", description = "The name of the pet.",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    String name,

    @NotNull
    @Valid
    @PetAgeValidation
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(name = "birthDate", example = "2010-09-07", description = "The date of birth of the pet.",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("birthDate")
    LocalDate birthDate,

    @NotNull
    @Valid
    @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    PetTypeDto type,

    @Min(0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1",
        description = "The ID of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id,

    @Nullable
    @Min(0)
    @Schema(name = "ownerId", accessMode = Schema.AccessMode.READ_ONLY, example = "1",
        description = "The ID of the pet's owner.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("ownerId")
    Integer ownerId,

    @NotNull
    @Valid
    @Schema(name = "visits", accessMode = Schema.AccessMode.READ_ONLY,
        description = "Vet visit bookings for this pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("visits")
    List<VisitDto> visits
) {
    public PetDto {
        Objects.requireNonNull(name, "name must not be null");
        if (name.length() > 30) {
            throw new IllegalArgumentException("name must not exceed 30 characters");
        }
        Objects.requireNonNull(birthDate, "birthDate must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(visits, "visits must not be null");
        visits = List.copyOf(visits);
        validateNonNegative(id, "id");
        validateNonNegative(ownerId, "ownerId");
    }

    private static void validateNonNegative(Integer value, String fieldName) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(fieldName + " must be non-negative");
        }
    }
}
