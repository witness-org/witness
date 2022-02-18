package com.witness.server.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.exercise.ExerciseHistoryDto;
import com.witness.server.dto.exercise.ExerciseHistoryEntryDto;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.mapper.ExerciseHistoryMapper;
import com.witness.server.mapper.ExerciseHistoryMapperImpl;
import com.witness.server.mapper.ExerciseLogMapperImpl;
import com.witness.server.mapper.SetLogMapperImpl;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.Comparators;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.time.ZonedDateTime;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ExerciseHistoryMapperImpl.class, ExerciseLogMapperImpl.class, SetLogMapperImpl.class})
public class ExerciseHistoryMapperTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/mapper/exercise-history-mapper-test/";

  @Autowired
  private ExerciseHistoryMapper mapper;

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogs.json", type = ExerciseLog[].class),
      @JsonFileSource(value = DATA_ROOT + "ExerciseHistoryEntryDtos.json", type = ExerciseHistoryEntryDto[].class)
  })
  void exerciseLogToHistoryEntryDto(ExerciseLog exerciseLog, ExerciseHistoryEntryDto entryDto) {
    assertThat(mapper.exerciseLogToHistoryEntryDto(exerciseLog))
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(entryDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogs.json", type = ExerciseLog[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "ExerciseHistoryEntryDtos.json", type = ExerciseHistoryEntryDto[].class, arrayToList = true)
  })
  void exerciseLogsToHistoryDtoEntries(List<ExerciseLog> exerciseLogs, List<ExerciseHistoryEntryDto> entryDtos) {
    assertThat(mapper.exerciseLogsToHistoryDtoEntries(exerciseLogs))
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(entryDtos);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "ExerciseLogs.json", type = ExerciseLog[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "ExerciseHistoryDto.json", type = ExerciseHistoryDto.class)
  })
  void exerciseLogsToHistoryDto(List<ExerciseLog> exerciseLogs, ExerciseHistoryDto historyDto) {
    assertThat(mapper.exerciseLogsToHistoryDto(exerciseLogs))
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(historyDto);
  }
}
