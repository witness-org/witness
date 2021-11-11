package com.witness.server.dto.workout;

import com.witness.server.enumeration.ResistanceBand;
import java.util.List;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public abstract class BaseSetLogDto {
  @Min(0)
  @Max(10)
  protected Integer rpe;

  @NotNull
  protected Long weightKg;

  @NotNull
  protected List<ResistanceBand> resistanceBands;
}
