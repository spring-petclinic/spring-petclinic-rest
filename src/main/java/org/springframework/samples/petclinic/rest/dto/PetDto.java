package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.samples.petclinic.rest.validation.PetAgeValidation;

/**
 * A pet.
 */
@Schema(name = "Pet", description = "A pet.")
@JsonTypeName("Pet")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-10-06T10:26:59.724080400+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record PetDto(
    @NotNull
    @Size(max = 30)
    @Schema(name = "name", example = "Leo", description = "The name of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    String name,

    @PetAgeValidation
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    @Valid
    @Schema(name = "birthDate", example = "2010-09-07", description = "The date of birth of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("birthDate")
    LocalDate birthDate,

    @NotNull
    @Valid
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

    @Valid
    @Schema(name = "visits", accessMode = Schema.AccessMode.READ_ONLY, description = "Vet visit bookings for this pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("visits")
    List<VisitDto> visits
) {

    public PetDto {
        if (name == null) {
            throw new IllegalArgumentException("name is required");
        }
        if (name.length() > 30) {
            throw new IllegalArgumentException("name length must not exceed 30");
        }
        if (birthDate == null) {
            throw new IllegalArgumentException("birthDate is required");
        }
        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }
        if (id != null && id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }
        if (ownerId != null && ownerId < 0) {
            throw new IllegalArgumentException("ownerId must be >= 0");
        }
        if (visits == null) {
            throw new IllegalArgumentException("visits is required");
        }
    }
}
