package com.witness.server.mapper;

import com.witness.server.dto.workout.AbstractSetLogCreateDto;
import com.witness.server.dto.workout.AbstractSetLogDto;
import com.witness.server.dto.workout.RepsSetLogCreateDto;
import com.witness.server.dto.workout.RepsSetLogDto;
import com.witness.server.dto.workout.TimeSetLogCreateDto;
import com.witness.server.dto.workout.TimeSetLogDto;
import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class SetLogMapper {

  // TODO subclass mappings can be simplified with a future MapStruct version (see also ExerciseMapper)
  //  (see https://mvnrepository.com/artifact/org.mapstruct/mapstruct and https://github.com/mapstruct/mapstruct/pull/2512)
  /**
   * Maps a creation DTO to a {@link SetLog object} based on the concrete class.
   *
   * @param setLog DTO to be mapped
   * @return mapped {@link SetLog object}
   */
  public SetLog createDtoToEntity(AbstractSetLogCreateDto setLog) {
    if (setLog instanceof TimeSetLogCreateDto) {
      return timeCreateDtoToEntity((TimeSetLogCreateDto) setLog);
    } else {
      return repsCreateDtoToEntity((RepsSetLogCreateDto) setLog);
    }
  }

  /**
   * Maps a DTO to a {@link SetLog object} based on the concrete class.
   *
   * @param setLog DTO to be mapped
   * @return mapped {@link SetLog object}
   */
  public SetLog dtoToEntity(AbstractSetLogDto setLog) {
    if (setLog instanceof TimeSetLogDto) {
      return timeDtoToEntity((TimeSetLogDto) setLog);
    } else {
      return repsDtoToEntity((RepsSetLogDto) setLog);
    }
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseLog", ignore = true)
  public abstract TimeSetLog timeCreateDtoToEntity(TimeSetLogCreateDto setLog);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseLog", ignore = true)
  public abstract RepsSetLog repsCreateDtoToEntity(RepsSetLogCreateDto setLog);

  public abstract TimeSetLog timeDtoToEntity(TimeSetLogDto setLog);

  public abstract RepsSetLog repsDtoToEntity(RepsSetLogDto setLog);
}
