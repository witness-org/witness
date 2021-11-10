package com.witness.server.dto.workout;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class WorkoutLogCreateDto extends BaseWorkoutLogDto {
  @NotNull
  private List<ExerciseLogCreateDto> exerciseLogs;
}
