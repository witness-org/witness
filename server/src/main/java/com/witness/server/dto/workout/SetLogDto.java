package com.witness.server.dto.workout;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = TimeSetLogDto.class, name = "time"),
    @JsonSubTypes.Type(value = RepsSetLogDto.class, name = "reps")
})
@Schema(
    description = "Represents an abstract supertype for different representations of a persisted set log.",
    subTypes = {TimeSetLogDto.class, RepsSetLogDto.class},
    discriminatorMapping = {
        @DiscriminatorMapping(value = "time", schema = TimeSetLogDto.class),
        @DiscriminatorMapping(value = "reps", schema = RepsSetLogDto.class)
    },
    discriminatorProperty = "type"
)
public abstract class SetLogDto extends BaseSetLogDto {
  @NotNull
  @Schema(description = "The ID of the set log.", example = "234")
  protected Long id;

  @NotNull
  @Min(1)
  @Schema(description = "The one-based position of this item in the collection of set logs in the respective exercise log.", example = "3")
  protected Integer position;

  @NotNull
  @Schema(description = "The ID of the exercise log this set is associated with.", example = "7")
  protected Long exerciseLogId;

  @Schema(description = "Determines the concrete type of the set log. See respective documentation for values designating each type.")
  protected String type;
}
