package com.witness.server.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "Represents an exercise create request.")
public class ExerciseCreateDto extends BaseExerciseDto {
}
