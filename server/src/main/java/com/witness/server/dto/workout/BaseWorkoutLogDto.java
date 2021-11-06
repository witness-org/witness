package com.witness.server.dto.workout;

import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class BaseWorkoutLogDto {
  @Positive
  private Integer durationMinutes;
}
