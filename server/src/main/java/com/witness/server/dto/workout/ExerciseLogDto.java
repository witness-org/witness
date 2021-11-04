package com.witness.server.dto.workout;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExerciseLogDto extends ExerciseReferenceDto {
  @NotNull
  private Long id;

  @Length(max = 1024)
  private String comment;

  @NotNull
  private Long workoutLogId;

  @NotNull
  private List<SetLogDto> setLogs;
}

