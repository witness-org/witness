package com.witness.server.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Provides methods for time zone-aware date and time handling.
 */
public interface TimeService {

  /**
   * Gets the current date and time in the configured primary time zone (as specified by {@link TimeService#getPrimaryTimeZone()} ()}).
   *
   * @return the current date and time in the primary time zone
   */
  ZonedDateTime getCurrentTime();

  /**
   * Gets the current date and time in the time zone specified by {@code timeZone}.
   *
   * @param timeZone defines the time zone of the {@link ZonedDateTime} to return
   * @return the current date and time in the primary time zone
   */
  ZonedDateTime getCurrentTime(ZoneId timeZone);

  /**
   * Gets the current date and time in UTC time.
   *
   * @return the current date and time in UTC time
   */
  ZonedDateTime getCurrentTimeUtc();

  /**
   * Gets the primary time zone that is considered by default when accessing date functions. This time zone is also used when serializing date time
   * values to JSON (see {@link com.witness.server.configuration.JacksonConfig}).
   *
   * @return the default {@link ZoneId}. Implementations may decide for themselves how this time zone is determined.
   */
  ZoneId getPrimaryTimeZone();

  /**
   * A shortcut to the {@link ZoneId} of UTC.
   *
   * @return the {@link ZoneId} of UTC.
   */
  ZoneId getUtcTimeZone();
}
