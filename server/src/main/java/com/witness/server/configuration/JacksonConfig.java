package com.witness.server.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.witness.server.service.TimeService;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Exposes beans to the ApplicationContext that are related to Jackson, most notably {@link ObjectMapper}s for JSON (de-)serialization.
 */
@Configuration
public class JacksonConfig {
  private final TimeService timeService;

  @Autowired
  public JacksonConfig(TimeService timeService) {
    this.timeService = timeService;
  }

  /**
   * Provides an {@link ObjectMapper} instance to the ApplicationContext.
   *
   * @return the {@link ObjectMapper} instance configured to use
   */
  @Primary
  @Bean
  public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
    return builder
        .featuresToEnable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, DeserializationFeature.ACCEPT_FLOAT_AS_INT)
        .indentOutput(true)
        .findModulesViaServiceLoader(true)
        .modules(new JavaTimeModule())
        .timeZone(TimeZone.getTimeZone(timeService.getPrimaryTimeZone())) // serialize ZonedDateTime in primaryTimeZone
        .build();
  }
}
