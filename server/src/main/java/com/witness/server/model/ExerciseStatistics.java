package com.witness.server.model;

import com.witness.server.entity.exercise.Exercise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates relevant exercise statistics based on the logs of a user.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseStatistics {

  private Exercise exercise;

  private Long estimatedOneRepMaxG;

  private Long maxWeightG;

  private Integer maxReps;

  private Integer maxSeconds;
}
