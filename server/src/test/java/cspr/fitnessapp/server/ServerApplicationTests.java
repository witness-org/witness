package cspr.fitnessapp.server;

import static org.assertj.core.api.Assertions.assertThat;

import cspr.fitnessapp.server.web.GreetingController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ServerApplicationTests {

  @Autowired
  private GreetingController controller;

  @Test
  void contextLoads() {
    assertThat(controller).isNotNull();
  }

}
