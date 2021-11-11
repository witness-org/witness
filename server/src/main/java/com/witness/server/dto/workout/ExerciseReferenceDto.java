package com.witness.server.dto.workout;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public abstract class ExerciseReferenceDto {
  @NotNull
  protected Long exerciseId;
}
