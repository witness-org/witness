package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Represents a request for the creation of a new workout log.")
public class WorkoutLogCreateDto extends BaseWorkoutLogDto {
  @NotNull
  @Valid
  @Schema(description = "The exercises to be logged which were completed during this workout.")
  private List<ExerciseLogCreateDto> exerciseLogs;
}
