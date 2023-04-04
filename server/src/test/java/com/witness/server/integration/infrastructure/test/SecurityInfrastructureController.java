package com.witness.server.integration.infrastructure.test;

import com.witness.server.web.meta.PublicApi;
import com.witness.server.web.meta.RequiresAdmin;
import com.witness.server.web.meta.RequiresPremium;
import com.witness.server.web.meta.RequiresPremiumOrAdmin;
import com.witness.server.web.meta.SecuredValidatedRestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This REST controller is only activated during integration tests and is used to test basic security functionalities such as authentication modes
 * (no authentication, regular users, premium or admin users) as well as basic validation enabled by the {@link SecuredValidatedRestController}
 * annotation.
 */
@SecuredValidatedRestController
@RequestMapping("/security-infrastructure")
@Profile("integration-test & !api-generation")
public class SecurityInfrastructureController {
  private final AtomicLong counter;

  public SecurityInfrastructureController() {
    counter = new AtomicLong();
  }

  @GetMapping
  public MessageDto message(@RequestParam(value = "name", defaultValue = "World") @Size(min = 3) @NotBlank String name) {
    return new MessageDto(counter.incrementAndGet(), "Hello, %s!".formatted(name));
  }

  @GetMapping("premiumData")
  @RequiresPremium
  public ResponseEntity<String> getPremiumData() {
    return ResponseEntity.ok("premiumData");
  }

  @GetMapping("adminData")
  @RequiresAdmin
  public ResponseEntity<String> getAdminData() {
    return ResponseEntity.ok("adminData");
  }

  @GetMapping("premiumOrAdminData")
  @RequiresPremiumOrAdmin
  public ResponseEntity<String> getPremiumOrAdminData() {
    return ResponseEntity.ok("premiumOrAdminData");
  }

  @GetMapping("public")
  @PublicApi
  public ResponseEntity<String> getPublicData() {
    return ResponseEntity.ok("publicData");
  }

  @PostMapping
  public MessageDto createMessage(@Valid @RequestBody MessageDto dto) {
    return dto;
  }

}
