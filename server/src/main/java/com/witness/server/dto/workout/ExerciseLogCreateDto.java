package com.witness.server.dto.workout;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExerciseLogCreateDto extends ExerciseReferenceDto {
  @Length(max = 1024)
  private String comment;

  @NotNull
  @Valid
  private List<SetLogCreateDto> setLogs;
}

