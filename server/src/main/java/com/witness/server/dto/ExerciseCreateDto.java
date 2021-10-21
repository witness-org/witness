package com.witness.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Represents an exercise create request.")
public class ExerciseCreateDto extends AbstractExerciseDto {
}
