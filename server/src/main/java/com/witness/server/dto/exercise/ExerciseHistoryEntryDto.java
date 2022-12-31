package com.witness.server.dto.exercise;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.witness.server.dto.workout.ExerciseLogDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString
@Schema(description = "Represents an entry in an exercise history.")
public class ExerciseHistoryEntryDto {
  // TODO allow 0 to 9 decimal places and timezone with and without :
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XX")
  @Schema(description = "Date and time at which the workout was logged.", example = "2021-10-08T14:15:55.300+0200")
  private ZonedDateTime loggedOn;

  @NotNull
  @Valid
  @Schema(description = "The log entry for the given exercise that has been logged.")
  ExerciseLogDto exerciseLog;
}
