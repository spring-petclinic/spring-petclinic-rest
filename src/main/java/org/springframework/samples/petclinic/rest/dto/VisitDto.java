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
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * A booking for a vet visit.
 */
@Schema(name = "Visit", description = "A booking for a vet visit.")
@JsonTypeName("Visit")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-10-06T10:26:59.724080400+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record VisitDto(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Valid
    @Schema(name = "date", example = "2013-01-01", description = "The date of the visit.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("date")
    @Nullable LocalDate date,

    @NotNull
    @Size(min = 1, max = 255)
    @Schema(name = "description", example = "rabies shot", description = "The description for the visit.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("description")
    String description,

    @Min(value = 0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the visit.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id,

    @NotNull
    @Min(value = 0)
    @Schema(name = "petId", example = "1", description = "The ID of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("petId")
    Integer petId
) {

    public VisitDto {
        if (description == null) {
            throw new IllegalArgumentException("description is required");
        }
        if (description.isEmpty() || description.length() > 255) {
            throw new IllegalArgumentException("description length must be between 1 and 255");
        }
        if (id != null && id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }
        if (petId == null) {
            throw new IllegalArgumentException("petId is required");
        }
        if (petId < 0) {
            throw new IllegalArgumentException("petId must be >= 0");
        }
    }
}
