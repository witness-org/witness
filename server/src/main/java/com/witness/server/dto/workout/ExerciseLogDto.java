package com.witness.server.dto.workout;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

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

  @NotBlank
  @Length(min = 1, max = 256)
  @Schema(description = "The name of the exercise that is referenced by this exercise log.", example = "7")
  protected String exerciseName;

  @NotNull
  @Schema(description = "The ID of the workout log this exercise log belongs to.", example = "18")
  private Long workoutLogId;

  @NotNull
  @Valid
  @Schema(description = "The sets that were logged this exercise.")
  private List<SetLogDto> setLogs;
}

