package com.witness.server.dto.exercise;

import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.MuscleGroup;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public abstract class BaseExerciseDto {
  @NotBlank
  @Length(min = 1, max = 256)
  @Schema(description = "The name of the exercise.", example = "Barbell Bench Press")
  protected String name;

  @Length(max = 1024)
  @Schema(description = "The description of the exercise.", example = "Lie down on the bench and push the bar up above your face.")
  protected String description;

  @NotEmpty
  @ArraySchema(schema = @Schema(description = "A list of muscle groups that the exercise trains.", example = "CHEST"))
  protected List<MuscleGroup> muscleGroups;

  @NotEmpty
  @ArraySchema(schema = @Schema(description = "A list of logging types that describe how the exercise can be logged.", example = "REPS"))
  protected List<LoggingType> loggingTypes;
}
