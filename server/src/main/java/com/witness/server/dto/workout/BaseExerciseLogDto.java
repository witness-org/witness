package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public abstract class BaseExerciseLogDto {
  @Length(max = 256)
  @Schema(description = "An optional comment accompanying the new exercise log.", example = "Skipped legs due to injury.")
  protected String comment;
}
