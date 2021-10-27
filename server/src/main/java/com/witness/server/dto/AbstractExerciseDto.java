package com.witness.server.dto;

import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.MuscleGroup;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode
public abstract class AbstractExerciseDto {
  @NotBlank
  @Length(min = 1, max = 256)
  @Schema(description = "The name of the exercise.", example = "Barbell Bench Press")
  private String name;

  @Length(max = 1024)
  @Schema(description = "The description of the exercise.", example = "Lie down on the bench and push the bar up above your face.")
  private String description;

  @NotNull
  @NotEmpty
  @ArraySchema(schema = @Schema(description = "A list of muscle groups that the exercise trains.", example = "CHEST"))
  private List<MuscleGroup> muscleGroups;

  @NotNull
  @NotEmpty
  @ArraySchema(schema = @Schema(description = "A list of logging types that describe how the exercise can be logged.", example = "REPS"))
  private List<LoggingType> loggingTypes;
}
