package com.witness.server.mapper;

import com.witness.server.dto.AbstractSetLogCreateDto;
import com.witness.server.dto.AbstractSetLogDto;
import com.witness.server.dto.RepsSetLogCreateDto;
import com.witness.server.dto.RepsSetLogDto;
import com.witness.server.dto.TimeSetLogCreateDto;
import com.witness.server.dto.TimeSetLogDto;
import com.witness.server.entity.RepsSetLog;
import com.witness.server.entity.SetLog;
import com.witness.server.entity.TimeSetLog;
import org.mapstruct.Mapper;

@Mapper
public abstract class SetLogMapper {

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

  public abstract TimeSetLog timeCreateDtoToEntity(TimeSetLogCreateDto setLog);

  public abstract RepsSetLog repsCreateDtoToEntity(RepsSetLogCreateDto setLog);

  public abstract TimeSetLog timeDtoToEntity(TimeSetLogDto setLog);

  public abstract RepsSetLog repsDtoToEntity(RepsSetLogDto setLog);
}
