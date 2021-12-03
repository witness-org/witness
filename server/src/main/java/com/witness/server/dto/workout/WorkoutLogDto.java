package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Represents a persisted new workout log.")
public class WorkoutLogDto extends BaseWorkoutLogDto {
  @NotNull
  @Schema(description = "The ID of the workout log.", example = "7")
  private Long id;

  @NotNull
  @Schema(description = "The ID of the user that logged the workout.", example = "9")
  private Long userId;

  @NotNull
  @Schema(description = "Date and time at which the workout was logged.", example = "2021-10-08T14:15:55.3007597+02:00")
  private ZonedDateTime loggedOn;

  @NotNull
  @Valid
  @Schema(description = "The exercises that were logged during this workout.")
  private List<ExerciseLogDto> exerciseLogs;
}
