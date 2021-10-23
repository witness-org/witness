package com.witness.server.configuration;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * POJO representation of {@code security} section in application properties.
 */
@Component
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {
  private Resource firebaseServiceAccountKey;
  private boolean checkTokenRevoked;
  private List<String> allowedOrigins;
  private List<String> allowedHeaders;
  private List<String> allowedMethods;
  private List<String> allowedPublicApis;
  private List<String> validRoles;
}
