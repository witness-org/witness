package com.witness.server.mapper;

import com.witness.server.dto.exercise.ExerciseStatisticsDto;
import com.witness.server.model.ExerciseStatistics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ExerciseStatisticsMapper {

  @Mapping(source = "exercise.id", target = "exerciseId")
  public abstract ExerciseStatisticsDto modelToDto(ExerciseStatistics model);
}
