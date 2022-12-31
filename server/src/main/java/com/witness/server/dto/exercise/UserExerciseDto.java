package com.witness.server.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Represents a persisted user exercise.")
public class UserExerciseDto extends ExerciseDto {
  @NotNull
  @Schema(description = "The name of the user who created the exercise.", example = "user123")
  private String createdBy;
}
