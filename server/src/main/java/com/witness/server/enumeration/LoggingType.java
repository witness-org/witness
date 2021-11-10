package com.witness.server.enumeration;

import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public enum LoggingType {
  REPS,
  TIME;

  private static final Map<Class<? extends SetLog>, LoggingType> KNOWN_LOGGING_TYPES = Map.of(
      RepsSetLog.class, REPS,
      TimeSetLog.class, TIME
  );

  /**
   * Determines the {@link LoggingType} associated with a concrete {@link SetLog} manifestation.
   *
   * @param clazz class of a {@link SetLog} derived type whose {@link LoggingType} should be determined
   * @return Returns the {@link LoggingType} member which corresponds to the concrete {@link SetLog} type represented by {@code clazz}.
   * @throws NoSuchElementException if there exists no known association between {@code clazz} and a {@link LoggingType} member
   */
  public static LoggingType fromLog(Class<? extends SetLog> clazz) {
    return Optional.ofNullable(KNOWN_LOGGING_TYPES.get(clazz))
        .orElseThrow(() -> new NoSuchElementException("There is no LoggingType defined for the class \"%s\".".formatted(clazz)));
  }
}
