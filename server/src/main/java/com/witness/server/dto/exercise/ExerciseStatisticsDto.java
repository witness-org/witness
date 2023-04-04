package com.witness.server.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Represents the statistics for a given exercise based on corresponding set logs by a specific user.")
public class ExerciseStatisticsDto {

  @NotNull
  @Schema(description = "ID of the exercise.", example = "1")
  private Long exerciseId;

  @Schema(description = "Estimated one-repetition maximum for the exercise in grams.", example = "100000")
  private Long estimatedOneRepMaxG;

  @NotNull
  @Schema(description = "Maximum weight that was logged for the exercise in grams.", example = "90000")
  private Long maxWeightG;

  @PositiveOrZero
  @Schema(description = "Maximum repetitions that were logged for the exercise in grams.", example = "20")
  private Integer maxReps;

  @PositiveOrZero
  @Schema(description = "Maximum time that was logged for the exercise in seconds.", example = "90")
  private Integer maxSeconds;
}
