package com.witness.server.mapper;

import com.witness.server.dto.exercise.ExerciseHistoryDto;
import com.witness.server.dto.exercise.ExerciseHistoryEntryDto;
import com.witness.server.entity.workout.ExerciseLog;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ExerciseLogMapper.class})
public abstract class ExerciseHistoryMapper {
  public ExerciseHistoryDto exerciseLogsToHistoryDto(List<ExerciseLog> exerciseLogs) {
    return new ExerciseHistoryDto(exerciseLogsToHistoryDtoEntries(exerciseLogs));
  }

  @Mapping(source = "workoutLog.loggedOn", target = "loggedOn")
  @Mapping(source = "exerciseLog", target = "exerciseLog")
  public abstract ExerciseHistoryEntryDto exerciseLogToHistoryEntryDto(ExerciseLog exerciseLog);

  public abstract List<ExerciseHistoryEntryDto> exerciseLogsToHistoryDtoEntries(List<ExerciseLog> exerciseLogs);
}
