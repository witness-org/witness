package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public abstract class BaseExerciseLogDto {
  @NotNull
  @Schema(description = "The ID of the exercise that is referenced by the new exercise log.", example = "5")
  protected Long exerciseId;

  @Length(max = 1024)
  @Schema(description = "An optional comment accompanying the new exercise log.", example = "Skipped legs due to injury.")
  protected String comment;
}
