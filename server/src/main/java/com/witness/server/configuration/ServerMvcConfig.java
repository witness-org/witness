package com.witness.server.configuration;

import com.witness.server.web.infrastructure.LoggingRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Provides the capability of overriding callback methods to customize the configuration of the Spring MVC.
 */
@Configuration
public class ServerMvcConfig implements WebMvcConfigurer {
  private final LoggingRequestInterceptor loggingRequestInterceptor;

  @Autowired
  public ServerMvcConfig(LoggingRequestInterceptor loggingRequestInterceptor) {
    this.loggingRequestInterceptor = loggingRequestInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loggingRequestInterceptor)
        .excludePathPatterns("/error");
  }
}
