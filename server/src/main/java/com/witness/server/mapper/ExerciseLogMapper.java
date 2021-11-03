package com.witness.server.mapper;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.workout.ExerciseLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ExerciseLogMapper {

  @Mapping(target = "position", ignore = true)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "comment", ignore = true)
  @Mapping(target = "workoutLog", ignore = true)
  @Mapping(target = "logs", ignore = true)
  public abstract ExerciseLog fromExercise(Exercise exercise);
}
