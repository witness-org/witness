package com.witness.server.service.impl;

import com.witness.server.service.TimeService;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TimeServiceImpl implements TimeService {
  @Value("${date-times.zone-id}")
  private String primaryZoneId;

  private ZoneId primaryTimeZone;
  private final ZoneId utcTimeZone = ZoneId.of("UTC");

  @PostConstruct
  private void init() {
    primaryTimeZone = ZoneId.of(primaryZoneId);
  }

  @Override
  public ZonedDateTime getCurrentTime() {
    return Instant.now().atZone(primaryTimeZone);
  }

  @Override
  public ZonedDateTime getCurrentTime(ZoneId timeZone) {
    if (timeZone == null) {
      throw new IllegalArgumentException("timeZone must not be null");
    }

    return Instant.now().atZone(timeZone);
  }

  @Override
  public ZonedDateTime getCurrentTimeUtc() {
    return Instant.now().atZone(utcTimeZone);
  }

  @Override
  public ZoneId getPrimaryTimeZone() {
    return primaryTimeZone;
  }

  @Override
  public ZoneId getUtcTimeZone() {
    return utcTimeZone;
  }
}
