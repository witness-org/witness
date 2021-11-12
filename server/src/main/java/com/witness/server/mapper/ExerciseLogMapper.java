package com.witness.server.mapper;

import com.witness.server.dto.workout.ExerciseLogCreateDto;
import com.witness.server.dto.workout.ExerciseLogDto;
import com.witness.server.entity.workout.ExerciseLog;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {SetLogMapper.class})
public abstract class ExerciseLogMapper {

  @Mapping(source = "exercise.id", target = "exerciseId")
  @Mapping(source = "exercise.name", target = "exerciseName")
  @Mapping(source = "workoutLog.id", target = "workoutLogId")
  public abstract ExerciseLogDto entityToDto(ExerciseLog exerciseLog);

  @Mapping(source = "exerciseId", target = "exercise.id")
  @Mapping(source = "setLogs", target = "logs")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "position", ignore = true)
  @Mapping(target = "workoutLog", ignore = true)
  public abstract ExerciseLog createDtoToEntity(ExerciseLogCreateDto exerciseLog);

  public abstract List<ExerciseLogDto> entitiesToDto(List<ExerciseLog> exerciseLogs);

  public abstract List<ExerciseLog> createDtosToEntities(List<ExerciseLogCreateDto> exerciseLogs);
}
