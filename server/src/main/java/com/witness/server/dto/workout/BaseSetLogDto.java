package com.witness.server.dto.workout;

import com.witness.server.enumeration.ResistanceBand;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public abstract class BaseSetLogDto {
  @Min(0)
  @Max(10)
  @Schema(description = "The Rate of Perceived Exertion (RPE) during this set.", example = "7")
  protected Integer rpe;

  @NotNull
  @Schema(description = "The weight that was used during this set in grams.", example = "65000")
  protected Long weightG;

  @NotNull
  @ArraySchema(schema = @Schema(description = "The resistance bands that have been used during this set.", example = "HEAVY"))
  protected List<ResistanceBand> resistanceBands;
}
