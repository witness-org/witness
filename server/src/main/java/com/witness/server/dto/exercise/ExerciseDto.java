package com.witness.server.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Represents a persisted exercise.")
public class ExerciseDto extends AbstractExerciseDto {
  @NotNull
  @Schema(description = "The ID of the exercise.", example = "1")
  private Long id;
}
