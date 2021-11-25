package com.witness.server.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.workout.RepsSetLogCreateDto;
import com.witness.server.dto.workout.RepsSetLogDto;
import com.witness.server.dto.workout.SetLogCreateDto;
import com.witness.server.dto.workout.SetLogDto;
import com.witness.server.dto.workout.TimeSetLogCreateDto;
import com.witness.server.dto.workout.TimeSetLogDto;
import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import com.witness.server.mapper.SetLogMapper;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;

class SetLogMapperTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/mapper/set-log-mapper-test/";
  private final SetLogMapper mapper = Mappers.getMapper(SetLogMapper.class);

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetLogCreateDtos.json", type = SetLogCreateDto[].class),
      @JsonFileSource(value = DATA_ROOT + "SetLogsFromCreateDtos.json", type = SetLog[].class)
  })
  void createDtoToEntity(SetLogCreateDto createDto, SetLog entity) {
    assertThat(mapper.createDtoToEntity(createDto)).usingRecursiveComparison().isEqualTo(entity);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetLogDtos.json", type = SetLogDto[].class),
      @JsonFileSource(value = DATA_ROOT + "SetLogs.json", type = SetLog[].class)
  })
  void dtoToEntity(SetLogDto dto, SetLog entity) {
    assertThat(mapper.dtoToEntity(dto)).usingRecursiveComparison().isEqualTo(entity);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetLogs.json", type = SetLog[].class),
      @JsonFileSource(value = DATA_ROOT + "SetLogDtos.json", type = SetLogDto[].class)
  })
  void entityToDto(SetLog entity, SetLogDto dto) {
    assertThat(mapper.entityToDto(entity)).usingRecursiveComparison().isEqualTo(dto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetLogs.json", type = SetLog[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "SetLogDtos.json", type = SetLogDto[].class, arrayToList = true)
  })
  void entitiesToDtos(List<SetLog> entities, List<SetLogDto> dtos) {
    assertThat(mapper.entitiesToDtos(entities)).usingRecursiveComparison().isEqualTo(dtos);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetLogCreateDtos.json", type = SetLogCreateDto[].class, arrayToList = true),
      @JsonFileSource(value = DATA_ROOT + "SetLogsFromCreateDtos.json", type = SetLog[].class, arrayToList = true)
  })
  void createDtosToEntities(List<SetLogCreateDto> createDtos, List<SetLog> entities) {
    assertThat(mapper.createDtosToEntities(createDtos)).usingRecursiveComparison().isEqualTo(entities);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "TimeSetLogCreateDtos.json", type = TimeSetLogCreateDto[].class),
      @JsonFileSource(value = DATA_ROOT + "SetLogsFromTimeSetCreateDtos.json", type = SetLog[].class)
  })
  void timeCreateDtoToEntity(TimeSetLogCreateDto dto, SetLog entity) {
    assertThat(mapper.timeCreateDtoToEntity(dto)).usingRecursiveComparison().isEqualTo(entity);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "RepsSetLogCreateDtos.json", type = RepsSetLogCreateDto[].class),
      @JsonFileSource(value = DATA_ROOT + "SetLogsFromRepsSetCreateDtos.json", type = SetLog[].class)
  })
  void repsCreateDtoToEntity(RepsSetLogCreateDto dto, SetLog entity) {
    assertThat(mapper.repsCreateDtoToEntity(dto)).usingRecursiveComparison().isEqualTo(entity);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "TimeSetLogs.json", type = TimeSetLog[].class),
      @JsonFileSource(value = DATA_ROOT + "TimeSetLogDtos.json", type = TimeSetLogDto[].class)
  })
  void timeEntityToDto(TimeSetLog entity, TimeSetLogDto dto) {
    assertThat(mapper.timeEntityToDto(entity)).usingRecursiveComparison().isEqualTo(dto);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "RepsSetLogs.json", type = RepsSetLog[].class),
      @JsonFileSource(value = DATA_ROOT + "RepsSetLogDtos.json", type = RepsSetLogDto[].class)
  })
  void repsEntityToDto(RepsSetLog entity, RepsSetLogDto dto) {
    assertThat(mapper.repsEntityToDto(entity)).usingRecursiveComparison().isEqualTo(dto);
  }
}
