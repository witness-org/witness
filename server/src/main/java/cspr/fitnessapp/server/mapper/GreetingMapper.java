package cspr.fitnessapp.server.mapper;

import cspr.fitnessapp.server.dto.GreetingDto;
import cspr.fitnessapp.server.entity.Greeting;
import org.mapstruct.Mapper;

@Mapper
public abstract class GreetingMapper {

  public abstract GreetingDto entityToDto(Greeting entity);

}
