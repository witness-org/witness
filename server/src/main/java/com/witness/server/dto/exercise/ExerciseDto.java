package com.witness.server.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Represents a persisted exercise.")
public class ExerciseDto extends BaseExerciseDto {
  @NotNull
  @Schema(description = "The ID of the exercise.", example = "1")
  protected Long id;
}
