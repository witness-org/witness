package com.witness.server.mapper;

import com.witness.server.dto.workout.WorkoutLogCreateDto;
import com.witness.server.dto.workout.WorkoutLogDto;
import com.witness.server.entity.user.User;
import com.witness.server.entity.workout.WorkoutLog;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ExerciseLogMapper.class})
public abstract class WorkoutLogMapper {

  @Mapping(target = "user")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "loggedOn", ignore = true)
  @Mapping(target = "durationMinutes", ignore = true)
  @Mapping(target = "exerciseLogs", ignore = true)
  public abstract WorkoutLog fromUser(User user);

  @Mapping(source = "user.id", target = "userId")
  public abstract WorkoutLogDto entityToDto(WorkoutLog workoutLog);

  public abstract List<WorkoutLogDto> entitiesToDtos(List<WorkoutLog> workoutLogs);

  @Mapping(target = "user", ignore = true)
  public abstract WorkoutLog createDtoToEntity(WorkoutLogCreateDto workoutLogCreateDto);
}
