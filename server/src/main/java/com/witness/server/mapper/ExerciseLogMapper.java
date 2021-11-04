package com.witness.server.mapper;

import com.witness.server.dto.workout.ExerciseLogDto;
import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.workout.ExerciseLog;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {SetLogMapper.class})
public abstract class ExerciseLogMapper {

  @Mapping(target = "position", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "comment", ignore = true)
  @Mapping(target = "workoutLog", ignore = true)
  @Mapping(target = "logs", ignore = true)
  public abstract ExerciseLog fromExercise(Exercise exercise);

  @Mapping(source = "exercise.id", target = "exerciseId")
  @Mapping(source = "workoutLog.id", target = "workoutLogId")
  public abstract ExerciseLogDto entityToDto(ExerciseLog exerciseLog);

  public abstract List<ExerciseLogDto> entitiesToDto(List<ExerciseLog> exerciseLog);
}
