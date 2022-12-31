package com.witness.server.dto.workout;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public abstract class BaseWorkoutLogDto {
  @NotNull
  @PositiveOrZero
  @Schema(description = "The duration of the workout in minutes.", example = "120")
  protected Integer durationMinutes;

  // TODO allow 0 to 9 decimal places and timezone with and without :
  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss[.SSS]XX")
  @Schema(description = "Date and time at which the workout was logged.", example = "2021-10-08T14:15:55.300+0200")
  private ZonedDateTime loggedOn;
}
