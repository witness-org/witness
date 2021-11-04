package com.witness.server.dto.workout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeSetLogDto.class, name = "time"),
    @JsonSubTypes.Type(value = RepsSetLogDto.class, name = "reps")
})
public abstract class SetLogDto extends BaseSetLogDto {
  @NotNull
  protected Long id;
}