package com.witness.server.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.ExerciseCreateDto;
import com.witness.server.dto.ExerciseDto;
import com.witness.server.dto.UserExerciseDto;
import com.witness.server.entity.Exercise;
import com.witness.server.entity.UserExercise;
import com.witness.server.mapper.ExerciseMapper;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;

class ExerciseMapperTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/mapper/exercise-mapper-test/";
  private final ExerciseMapper mapper = Mappers.getMapper(ExerciseMapper.class);

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercises_1-2.json", type = Exercise[].class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDtos_1-2.json", type = ExerciseDto[].class)
  })
  void entityToDto(Exercise entity, ExerciseDto dto) {
    assertThat(mapper.entityToDto(entity)).isEqualTo(dto);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercises_1-2.json", type = UserExercise[].class),
      @JsonFileSource(value = DATA_ROOT + "UserExerciseDtos_1-2.json", type = UserExerciseDto[].class)
  })
  void userEntityToDto(UserExercise entity, UserExerciseDto dto) {
    assertThat(mapper.userEntityToDto(entity)).isEqualTo(dto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercises_1-2.json", type = Exercise[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "ExerciseDtos_1-2.json", type = ExerciseDto[].class, arrayToList = true)
  })
  void entitiesToDtos(List<Exercise> entity, List<ExerciseDto> dto) {
    assertThat(mapper.entitiesToDtos(entity)).usingRecursiveComparison().isEqualTo(dto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "CreatedExercise1.json", type = Exercise.class),
  })
  void createDtoToEntity(ExerciseCreateDto dto, Exercise entity) {
    assertThat(mapper.createDtoToEntity(dto)).isEqualTo(entity);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseCreateDto1.json", type = ExerciseCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "CreatedUserExercise1.json", type = UserExercise.class),
  })
  void createDtoToUserEntity(ExerciseCreateDto dto, UserExercise entity) {
    assertThat(mapper.createDtoToUserEntity(dto)).isEqualTo(entity);
  }
}
