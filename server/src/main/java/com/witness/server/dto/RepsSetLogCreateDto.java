package com.witness.server.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RepsSetLogCreateDto extends AbstractSetLogCreateDto {
  @NotNull
  @Min(1)
  private Integer reps;
}
