package com.witness.server.mapper;

import com.witness.server.dto.UserCreateDto;
import com.witness.server.dto.UserDto;
import com.witness.server.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class UserMapper {
  public abstract UserDto entityToDto(User user);

  public abstract User dtoToEntity(UserDto user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "firebaseId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "modifiedAt", ignore = true)
  public abstract User createDtoToEntity(UserCreateDto user);
}
