package com.witness.server.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.dto.UserCreateDto;
import com.witness.server.dto.UserDto;
import com.witness.server.entity.User;
import com.witness.server.mapper.UserMapper;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.mapstruct.factory.Mappers;

class UserMapperTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/mapper/user-mapper-test/";
  private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Users_1-2.json", type = User[].class),
      @JsonFileSource(value = DATA_ROOT + "UserDtos_1-2.json", type = UserDto[].class, arrayToList = true)
  })
  void entityToDto(User[] entities, List<UserDto> dtos) {
    for (var i = 0; i < entities.length; i++) {
      assertThat(mapper.entityToDto(entities[i])).isEqualTo(dtos.get(i));
    }
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserDtos_1-2.json", type = UserDto[].class),
      @JsonFileSource(value = DATA_ROOT + "Users_1-2.json", type = User[].class),
  })
  void dtoToEntity(UserDto dto, User entity) {
    assertThat(mapper.dtoToEntity(dto)).isEqualTo(entity);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDto1.json", type = UserCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "User3.json", type = User.class)
  })
  void createDtoToEntity(UserCreateDto createDto, User entity) {
    assertThat(mapper.createDtoToEntity(createDto)).isEqualTo(entity);
  }
}
