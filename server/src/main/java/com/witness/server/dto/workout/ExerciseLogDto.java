package com.witness.server.dto.workout;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExerciseLogDto extends ExerciseReferenceDto {
  @NotNull
  private Long id;

  @NotNull
  @Min(1)
  protected Integer position;

  @NotBlank
  @Length(min = 1, max = 256)
  protected String exerciseName;

  @Length(max = 1024)
  private String comment;

  @NotNull
  private Long workoutLogId;

  @NotNull
  private List<SetLogDto> setLogs;
}

