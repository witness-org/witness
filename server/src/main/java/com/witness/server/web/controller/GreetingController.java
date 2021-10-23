package com.witness.server.web.controller;

import com.witness.server.dto.GreetingDto;
import com.witness.server.entity.Greeting;
import com.witness.server.mapper.GreetingMapper;
import com.witness.server.web.meta.PublicApi;
import com.witness.server.web.meta.RequiresAdmin;
import com.witness.server.web.meta.RequiresPremium;
import com.witness.server.web.meta.RequiresPremiumOrAdmin;
import com.witness.server.web.meta.SecuredValidatedRestController;
import java.util.concurrent.atomic.AtomicLong;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SecuredValidatedRestController
@RequestMapping("/greeting")
public class GreetingController {
  private final AtomicLong counter;
  private final GreetingMapper greetingMapper;

  @Autowired
  public GreetingController(GreetingMapper greetingMapper) {
    this.greetingMapper = greetingMapper;
    this.counter = new AtomicLong();
  }

  @GetMapping
  public GreetingDto greeting(@RequestParam(value = "name", defaultValue = "World!") @Size(min = 3) @NotBlank String name) {
    var greeting = new Greeting(counter.incrementAndGet());
    greeting.setContent(name + ". This is a \"regular\" method, i.e. an authenticated user (with or without role) can access it");
    return greetingMapper.entityToDto(greeting);
  }

  @GetMapping("premiumData")
  @RequiresPremium
  public ResponseEntity<String> getPremiumData() {
    return ResponseEntity.ok("You have successfully called a method which can only be accessed by premium users.");
  }

  @GetMapping("adminData")
  @RequiresAdmin
  public ResponseEntity<String> getAdminData() {
    return ResponseEntity.ok("You have successfully called a method which can only be accessed by admin users.");
  }

  @GetMapping("premiumOrAdminData")
  @RequiresPremiumOrAdmin
  public ResponseEntity<String> getPremiumOrAdminData() {
    return ResponseEntity.ok("You have successfully called a method which can be accessed by premium or admin users.");
  }

  @GetMapping("public")
  @PublicApi
  public ResponseEntity<String> getPublicData() {
    return ResponseEntity.ok("You have successfully called a method which can be accessed by anyone, even without authorization. "
        + "This has been explicitly allowed, see application.yml (property security.allowed-public-apis). "
        + "This works even though we are in a @SecuredValidatedRestController because the \"Secured\" part"
        + "is only relevant for Swagger UI and the generated Open API 3.0 specification."
        + "Please note the missing padlock in Swagger UI for this operation. Even if you are logged in with a bearer token, no token "
        + "will be sent along with the request, because the @PublicApi annotation tells Swagger that no token is required.");
  }

  @PostMapping
  public GreetingDto createGreeting(@Valid @RequestBody GreetingDto dto) {
    return dto;
  }
}
