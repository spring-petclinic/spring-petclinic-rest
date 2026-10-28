package org.springframework.samples.petclinic.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.rest.dto.PetTypeDto;
import org.springframework.samples.petclinic.rest.validation.PetAgeValidation;

import java.time.LocalDate;
import java.util.List;

/**
 * A pet.
 * <p>
 * Hand-written record that replaces the generated {@code PetDto} (see {@code schemaMappings} in the pom).
 * The compact constructor rejects null/out-of-bounds values and defensively copies {@code visits}.
 * The pet-age rule stays in {@link PetAgeValidation}.
 *
 * @param name      the name of the pet (at most 30 characters)
 * @param birthDate the date of birth of the pet
 * @param type      the pet type
 * @param id        the ID of the pet
 * @param ownerId   the ID of the pet's owner
 * @param visits    vet visit bookings for this pet (never null, unmodifiable)
 */
@Schema(name = "Pet", description = "A pet.")
@JsonTypeName("Pet")
public record PetDto(
    @NotNull @Size(max = 30)
    @Schema(name = "name", example = "Leo", description = "The name of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    String name,

    @NotNull @Valid
    @PetAgeValidation
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(name = "birthDate", example = "2010-09-07", description = "The date of birth of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("birthDate")
    LocalDate birthDate,

    @NotNull @Valid
    @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    PetTypeDto type,

    @Min(value = 0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    @Nullable Integer id,

    @Min(value = 0)
    @Schema(name = "ownerId", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the pet's owner.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("ownerId")
    @Nullable Integer ownerId,

    @Schema(name = "visits", accessMode = Schema.AccessMode.READ_ONLY, description = "Vet visit bookings for this pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("visits")
    List<@Valid VisitDto> visits
) {

    public PetDto {
        if (name == null || name.length() > 30) {
            throw new IllegalArgumentException("name must not be null and must be at most 30 characters");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("birthDate must not be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (id != null && id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }
        if (ownerId != null && ownerId < 0) {
            throw new IllegalArgumentException("ownerId must be >= 0");
        }
        visits = visits == null ? List.of() : List.copyOf(visits);
    }
}
