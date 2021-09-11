package com.witness.server.mapper;

import com.witness.server.dto.GreetingDto;
import com.witness.server.entity.Greeting;
import org.mapstruct.Mapper;

@Mapper
public abstract class GreetingMapper {

  public abstract GreetingDto entityToDto(Greeting entity);

}
