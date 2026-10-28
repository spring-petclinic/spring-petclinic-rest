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

import java.time.LocalDate;

/**
 * A booking for a vet visit.
 * <p>
 * Hand-written record that replaces the generated {@code VisitDto} (see {@code schemaMappings} in the pom).
 * The compact constructor enforces the same bounds as the OpenAPI schema, so invalid data fails fast.
 *
 * @param date        the date of the visit
 * @param description the description for the visit (1..255 characters)
 * @param id          the ID of the visit
 * @param petId       the ID of the pet
 */
@Schema(name = "Visit", description = "A booking for a vet visit.")
@JsonTypeName("Visit")
public record VisitDto(
    @Valid
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(name = "date", example = "2013-01-01", description = "The date of the visit.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("date")
    @Nullable LocalDate date,

    @NotNull @Size(min = 1, max = 255)
    @Schema(name = "description", example = "rabies shot", description = "The description for the visit.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("description")
    String description,

    @Min(value = 0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the visit.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    @Nullable Integer id,

    @NotNull @Min(value = 0)
    @Schema(name = "petId", example = "1", description = "The ID of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("petId")
    Integer petId
) {

    public VisitDto {
        if (description == null || description.isEmpty() || description.length() > 255) {
            throw new IllegalArgumentException("description must be between 1 and 255 characters");
        }
        if (petId == null || petId < 0) {
            throw new IllegalArgumentException("petId must not be null and must be >= 0");
        }
        if (id != null && id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }
    }
}
