package com.witness.server.dto.workout;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RepsSetLogDto extends SetLogDto {
  @NotNull
  @Min(1)
  private Integer reps;
}
