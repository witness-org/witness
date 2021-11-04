package com.witness.server.dto.workout;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimeSetLogDto extends SetLogDto {
  @NotNull
  @Min(1)
  private Long seconds;
}