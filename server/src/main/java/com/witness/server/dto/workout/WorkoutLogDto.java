package com.witness.server.dto.workout;

import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class WorkoutLogDto {
  @NotNull
  private Long id;

  @NotNull
  private Long userId;

  @NotNull
  private ZonedDateTime loggedOn;

  @Positive
  private Integer durationMinutes;

  @NotNull
  private List<ExerciseLogDto> exerciseLogs;
}
