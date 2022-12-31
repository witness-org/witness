package com.witness.server.dto.workout;

import com.witness.server.dto.exercise.ExerciseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Represents a persisted exercise log.")
public class ExerciseLogDto extends BaseExerciseLogDto {
  @NotNull
  @Schema(description = "The ID of the exercise log.", example = "1")
  private Long id;

  @NotNull
  @Min(1)
  @Schema(description = "The one-based position of this item in the collection of exercise logs in the respective workout log.", example = "2")
  protected Integer position;

  @Valid
  @NotNull
  @Schema(description = "The exercise that is referenced by this exercise log.")
  protected ExerciseDto exercise;

  @NotNull
  @Schema(description = "The ID of the workout log this exercise log belongs to.", example = "18")
  private Long workoutLogId;

  @NotNull
  @Valid
  @Schema(description = "The sets that were logged this exercise.")
  private List<SetLogDto> setLogs;
}

