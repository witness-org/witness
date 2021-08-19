package cspr.fitnessapp.server.web;

import cspr.fitnessapp.server.dto.GreetingDto;
import cspr.fitnessapp.server.entity.Greeting;
import cspr.fitnessapp.server.mapper.GreetingMapper;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/greeting")
public class GreetingController {
  private final AtomicLong counter = new AtomicLong();

  private final GreetingMapper greetingMapper;

  @Autowired
  public GreetingController(GreetingMapper greetingMapper) {
    this.greetingMapper = greetingMapper;
  }

  @GetMapping
  public GreetingDto greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    var greeting = new Greeting(counter.incrementAndGet());
    greeting.setContent(name);
    return greetingMapper.entityToDto(greeting);
  }
}
