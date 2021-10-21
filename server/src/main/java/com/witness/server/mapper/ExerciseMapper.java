package com.witness.server.mapper;

import com.witness.server.dto.ExerciseCreateDto;
import com.witness.server.dto.ExerciseDto;
import com.witness.server.dto.UserExerciseDto;
import com.witness.server.entity.Exercise;
import com.witness.server.entity.UserExercise;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ExerciseMapper {

  public abstract ExerciseDto entityToDto(Exercise exercise);

  @Mapping(source = "createdBy.id", target = "createdByUserId")
  public abstract UserExerciseDto userEntityToDto(UserExercise exercise);

  @Mapping(target = "id", ignore = true)
  public abstract Exercise createDtoToEntity(ExerciseCreateDto exercise);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  public abstract UserExercise createDtoToUserEntity(ExerciseCreateDto exercise);

  public abstract List<ExerciseDto> entitiesToDtos(List<Exercise> exercises);
}
