package com.witness.server.mapper;

import com.witness.server.entity.User;
import com.witness.server.entity.WorkoutLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class WorkoutLogMapper {

  @Mapping(target = "user")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "loggedOn", ignore = true)
  @Mapping(target = "durationMinutes", ignore = true)
  @Mapping(target = "exerciseLogs", ignore = true)
  public abstract WorkoutLog fromUser(User user);
}
