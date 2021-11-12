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
@Schema(description = "Represents a request to create a reps set. The discriminating \"type\" property must be equal to \"timeCreate\".")
public class TimeSetLogCreateDto extends SetLogCreateDto {
  @NotNull
  @Min(1)
  @Schema(description = "Number of seconds the set lasted.", example = "45")
  private Integer seconds;

  @Schema(description = "Determines the concrete type of the set log. Only valid value for this subtype: \"timeCreate\".", example = "timeCreate")
  @Override
  public String getType() {
    return type;
  }
}
