package com.witness.server.enumeration;

import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public enum LoggingType {
  REPS,
  TIME;

  private static final Map<Class<? extends SetLog>, LoggingType> LOGGING_TYPE_ASSOCIATIONS = Map.of(
      RepsSetLog.class, REPS,
      TimeSetLog.class, TIME
  );

  /**
   * Determines the {@link LoggingType} associated with a concrete {@link SetLog} instance.
   *
   * @param setLog {@link SetLog} instance whose {@link LoggingType} should be determined
   * @return Returns the {@link LoggingType} member which corresponds to the concrete {@link SetLog} type represented by {@code setLog}.
   * @throws NullPointerException   if {@code setLog} is {@code null}
   * @throws NoSuchElementException if there exists no known association between {@code clazz} and a {@link LoggingType} member
   */
  public static LoggingType fromSetLog(SetLog setLog) {
    Objects.requireNonNull(setLog, "SetLog instance to determine LoggingType from must not be null.");
    return Optional.ofNullable(LOGGING_TYPE_ASSOCIATIONS.get(setLog.getClass()))
        .orElseThrow(() -> new NoSuchElementException("There is no LoggingType defined for the class \"%s\".".formatted(setLog.getClass())));
  }
}
