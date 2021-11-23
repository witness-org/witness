package com.witness.server.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.workout.ExerciseLogCreateDto;
import com.witness.server.dto.workout.ExerciseLogDto;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.mapper.ExerciseLogMapper;
import com.witness.server.mapper.ExerciseLogMapperImpl;
import com.witness.server.mapper.SetLogMapperImpl;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ExerciseLogMapperImpl.class, SetLogMapperImpl.class})
class ExerciseLogMapperTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/mapper/exercise-log-mapper-test/";

  @Autowired
  private ExerciseLogMapper mapper;

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogs.json", type = ExerciseLog[].class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogDtos.json", type = ExerciseLogDto[].class)
  })
  void entityToDto(ExerciseLog entity, ExerciseLogDto dto) {
    assertThat(mapper.entityToDto(entity)).usingRecursiveComparison().isEqualTo(dto);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDtos.json", type = ExerciseLogCreateDto[].class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogsFromCreateDtos.json", type = ExerciseLog[].class)
  })
  void createDtoToEntity(ExerciseLogCreateDto entity, ExerciseLog dto) {
    assertThat(mapper.createDtoToEntity(entity)).usingRecursiveComparison().isEqualTo(dto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogs.json", type = ExerciseLog[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogDtos.json", type = ExerciseLogDto[].class, arrayToList = true)
  })
  void entitiesToDtos(List<ExerciseLog> entities, List<ExerciseLogDto> dtos) {
    assertThat(mapper.entitiesToDtos(entities)).usingRecursiveComparison().isEqualTo(dtos);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogCreateDtos.json", type = ExerciseLogCreateDto[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogsFromCreateDtos.json", type = ExerciseLog[].class, arrayToList = true)
  })
  void createDtosToEntities(List<ExerciseLogCreateDto> entities, List<ExerciseLog> dtos) {
    assertThat(mapper.createDtosToEntities(entities)).usingRecursiveComparison().isEqualTo(dtos);
  }
}
