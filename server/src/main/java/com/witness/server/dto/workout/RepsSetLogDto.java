package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Represents a persisted reps set. The discriminating \"type\" property must be equal to \"reps\".")
public class RepsSetLogDto extends SetLogDto {
  @NotNull
  @Min(1)
  @Schema(description = "The number of conducted reps logged for this set.", example = "15")
  private Integer reps;

  @Schema(description = "Determines the concrete type of the set log. Only valid value for this subtype: \"reps\".", example = "reps")
  @Override
  public String getType() {
    return type;
  }
}
