package com.witness.server.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeSetLogCreateDto.class, name = "time"),
    @JsonSubTypes.Type(value = RepsSetLogCreateDto.class, name = "reps")
})
public abstract class AbstractSetLogCreateDto extends AbstractSetLogSuperDto {
}
