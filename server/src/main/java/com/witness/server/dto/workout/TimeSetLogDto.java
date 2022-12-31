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
@Schema(description = "Represents a persisted time set. The discriminating \"type\" property must be equal to \"time\".")
public class TimeSetLogDto extends SetLogDto {
  @NotNull
  @Min(1)
  @Schema(description = "Number of seconds the set lasted.", example = "45")
  private Long seconds;

  @Schema(description = "Determines the concrete type of the set log. Only valid value for this subtype: \"time\".", example = "time")
  @Override
  public String getType() {
    return type;
  }
}
