package com.witness.server.dto.workout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeSetLogCreateDto.class, name = "timeCreate"),
    @JsonSubTypes.Type(value = RepsSetLogCreateDto.class, name = "repsCreate")
})
public abstract class SetLogCreateDto extends BaseSetLogDto {
}
