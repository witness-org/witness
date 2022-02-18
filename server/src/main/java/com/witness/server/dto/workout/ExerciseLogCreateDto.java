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
@Schema(description = "Represents a request for the creation of a new exercise log.")
public class ExerciseLogCreateDto extends BaseExerciseLogDto {
  @NotNull
  @Schema(description = "The ID of the exercise that is referenced by the new exercise log.", example = "5")
  protected Long exerciseId;

  @NotNull
  @Valid
  @Schema(description = "The sets to be logged which were completed with this exercise.")
  private List<SetLogCreateDto> setLogs;
}

