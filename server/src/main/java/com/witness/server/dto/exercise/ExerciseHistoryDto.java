package com.witness.server.dto.exercise;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
@Schema(description = "Represents an exercise history, i.e. a collection of log entries for a specific exercise.")
public class ExerciseHistoryDto {
  @NotNull
  @Valid
  @Schema(description = "The log history items for the given exercise.")
  private List<ExerciseHistoryEntryDto> entries;
}
