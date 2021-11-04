package com.witness.server.dto.workout;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public abstract class ExerciseReferenceDto {
  @NotNull
  @Min(1)
  protected Integer position;

  @NotNull
  protected Long exerciseId;
}
