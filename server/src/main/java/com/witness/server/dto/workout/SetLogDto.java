package com.witness.server.dto.workout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeSetLogDto.class, name = "time"),
    @JsonSubTypes.Type(value = RepsSetLogDto.class, name = "reps")
})
public abstract class SetLogDto extends BaseSetLogDto {
  @NotNull
  protected Long id;

  @NotNull
  @Min(1)
  protected Integer position;

  @NotNull
  protected Long exerciseLogId;
}
