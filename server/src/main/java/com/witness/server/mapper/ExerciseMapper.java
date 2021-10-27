package com.witness.server.mapper;

import com.witness.server.dto.ExerciseCreateDto;
import com.witness.server.dto.ExerciseDto;
import com.witness.server.dto.UserExerciseDto;
import com.witness.server.entity.Exercise;
import com.witness.server.entity.UserExercise;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class ExerciseMapper {

  public abstract ExerciseDto entityToDto(Exercise exercise);

  public abstract Exercise dtoToEntity(ExerciseDto exerciseDto);

  @Mapping(source = "createdBy.username", target = "createdBy")
  public abstract UserExerciseDto userEntityToDto(UserExercise exercise);

  @Mapping(target = "id", ignore = true)
  public abstract Exercise createDtoToEntity(ExerciseCreateDto exercise);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  public abstract UserExercise createDtoToUserEntity(ExerciseCreateDto exercise);

  /**
   * Maps a list containing exercises (both initial and user exercises) to a list of corresponding DTOs.
   *
   * @param exercises list of exercises to map
   * @return list of DTOs; if a source item is of type {@link UserExercise}, the mapped target item is of type {@link UserExerciseDto},
   *     otherwise {@link ExerciseDto}
   */
  public List<ExerciseDto> entitiesToDtos(List<Exercise> exercises) {
    if (exercises == null) {
      return null;
    }

    return exercises.stream().map(this::entityToDtoTypeAware).collect(Collectors.toList());
  }

  private ExerciseDto entityToDtoTypeAware(Exercise exercise) {
    if (exercise instanceof UserExercise) {
      return userEntityToDto((UserExercise) exercise);
    }
    return entityToDto(exercise);
  }
}
