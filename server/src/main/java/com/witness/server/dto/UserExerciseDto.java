package com.witness.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Represents a persisted user exercise.")
public class UserExerciseDto extends ExerciseDto {
  @NotNull
  @Schema(description = "The ID of the user who created the exercise.", example = "1")
  private Long createdByUserId;
}