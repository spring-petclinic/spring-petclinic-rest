package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A booking for a vet visit.
 */
@Schema(name = "Visit", description = "A booking for a vet visit.")
public record VisitDto(
    @Nullable
    @Valid
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(name = "date", example = "2013-01-01", description = "The date of the visit.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("date")
    LocalDate date,

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(name = "description", example = "rabies shot", description = "The description for the visit.",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("description")
    String description,

    @Min(0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1",
        description = "The ID of the visit.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id,

    @NotNull
    @Min(0)
    @Schema(name = "petId", example = "1", description = "The ID of the pet.",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("petId")
    Integer petId
) {
    public VisitDto {
        Objects.requireNonNull(description, "description must not be null");
        if (description.isEmpty() || description.length() > 255) {
            throw new IllegalArgumentException("description must contain between 1 and 255 characters");
        }
        Objects.requireNonNull(petId, "petId must not be null");
        validateNonNegative(id, "id");
        validateNonNegative(petId, "petId");
    }

    private static void validateNonNegative(Integer value, String fieldName) {
        if (value != null && value < 0) {
            throw new IllegalArgumentException(fieldName + " must be non-negative");
        }
    }
}
