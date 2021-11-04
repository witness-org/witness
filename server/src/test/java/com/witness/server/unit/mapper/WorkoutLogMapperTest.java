package com.witness.server.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.workout.WorkoutLogDto;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.mapper.ExerciseLogMapperImpl;
import com.witness.server.mapper.SetLogMapperImpl;
import com.witness.server.mapper.WorkoutLogMapper;
import com.witness.server.mapper.WorkoutLogMapperImpl;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {WorkoutLogMapperImpl.class, ExerciseLogMapperImpl.class, SetLogMapperImpl.class})
class WorkoutLogMapperTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/mapper/workout-log-mapper-test/";

  @Autowired
  private WorkoutLogMapper mapper;

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogs_1-2-3.json", type = WorkoutLog[].class),
      @JsonFileSource(value = DATA_ROOT + "WorkoutLogDtos_1-2-3.json", type = WorkoutLogDto[].class)
  })
  void entityToDto(WorkoutLog entity, WorkoutLogDto dto) {
    assertThat(mapper.entityToDto(entity))
        .usingRecursiveComparison()
        .isEqualTo(dto);
  }
}
