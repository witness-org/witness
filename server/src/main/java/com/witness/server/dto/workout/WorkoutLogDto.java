package com.witness.server.dto.workout;

import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WorkoutLogDto extends BaseWorkoutLogDto {
  @NotNull
  private Long id;

  @NotNull
  private Long userId;

  @NotNull
  private ZonedDateTime loggedOn;

  @NotNull
  private List<ExerciseLogDto> exerciseLogs;
}
