package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Represents a request to create a reps set. The discriminating \"type\" property must be equal to \"repsCreate\".")
public class RepsSetLogCreateDto extends SetLogCreateDto {
  @NotNull
  @Min(1)
  @Schema(description = "The number of conducted reps to be logged for this set.", example = "15")
  private Integer reps;

  @Schema(description = "Determines the concrete type of the set log. Only valid value for this subtype: \"repsCreate\".", example = "repsCreate")
  @Override
  public String getType() {
    return type;
  }
}
