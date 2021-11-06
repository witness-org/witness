package com.witness.server.mapper;

import com.witness.server.dto.workout.RepsSetLogCreateDto;
import com.witness.server.dto.workout.RepsSetLogDto;
import com.witness.server.dto.workout.SetLogCreateDto;
import com.witness.server.dto.workout.SetLogDto;
import com.witness.server.dto.workout.TimeSetLogCreateDto;
import com.witness.server.dto.workout.TimeSetLogDto;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import java.util.List;
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
  public SetLog createDtoToEntity(SetLogCreateDto setLog) {
    if (setLog instanceof TimeSetLogCreateDto) {
      return timeCreateDtoToEntity((TimeSetLogCreateDto) setLog);
    } else {
      return repsCreateDtoToEntity((RepsSetLogCreateDto) setLog);
    }
  }

  /**
   * Maps a DTO to a {@link SetLog} object based on the concrete class.
   *
   * @param setLog DTO to be mapped
   * @return mapped {@link SetLog} object
   */
  public SetLog dtoToEntity(SetLogDto setLog) {
    if (setLog instanceof TimeSetLogDto) {
      return timeDtoToEntity((TimeSetLogDto) setLog);
    } else {
      return repsDtoToEntity((RepsSetLogDto) setLog);
    }
  }

  /**
   * Maps a {@link SetLog} object to a {@link SetLogDto} based based on the concrete class.
   *
   * @param setLog entity to be mapped
   * @return mapped {@link SetLogDto} object
   */
  public SetLogDto entityToDto(SetLog setLog) {
    if (setLog instanceof TimeSetLog) {
      return timeEntityToDto((TimeSetLog) setLog);
    } else {
      return repsEntityToDto((RepsSetLog) setLog);
    }
  }

  public abstract List<SetLogDto> entitiesToDtos(List<SetLog> setLogs);

  public abstract List<SetLog> createDtosToEntities(List<SetLogCreateDto> setLogs);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseLog", ignore = true)
  public abstract TimeSetLog timeCreateDtoToEntity(TimeSetLogCreateDto setLog);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "exerciseLog", ignore = true)
  public abstract RepsSetLog repsCreateDtoToEntity(RepsSetLogCreateDto setLog);

  public abstract TimeSetLog timeDtoToEntity(TimeSetLogDto setLog);

  public abstract RepsSetLog repsDtoToEntity(RepsSetLogDto setLog);

  @Mapping(source = "exerciseLog.id", target = "exerciseLogId")
  public abstract TimeSetLogDto timeEntityToDto(TimeSetLog setLog);

  @Mapping(source = "exerciseLog.id", target = "exerciseLogId")
  public abstract RepsSetLogDto repsEntityToDto(RepsSetLog setLog);
}
