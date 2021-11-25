package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public abstract class BaseWorkoutLogDto {
  @Positive
  @Schema(description = "The duration of the workout in minutes.", example = "120")
  protected Integer durationMinutes;
}
