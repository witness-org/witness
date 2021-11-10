package com.witness.server.dto.workout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeSetLogCreateDto.class, name = "timeCreate"),
    @JsonSubTypes.Type(value = RepsSetLogCreateDto.class, name = "repsCreate")
})
@Schema(
    subTypes = {TimeSetLogCreateDto.class, RepsSetLogCreateDto.class},
    discriminatorMapping = {
        @DiscriminatorMapping(value = "timeCreate", schema = TimeSetLogCreateDto.class),
        @DiscriminatorMapping(value = "repsCreate", schema = RepsSetLogCreateDto.class)
    },
    discriminatorProperty = "type"
)
public abstract class SetLogCreateDto extends BaseSetLogDto {
}
