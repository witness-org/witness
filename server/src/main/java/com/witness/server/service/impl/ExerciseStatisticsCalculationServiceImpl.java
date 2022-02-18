package com.witness.server.service.impl;

import com.witness.server.service.ExerciseStatisticsCalculationService;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ExerciseStatisticsCalculationServiceImpl implements ExerciseStatisticsCalculationService {
  @Override
  public Optional<Long> getEstimatedOneRepMax(Long weight, Integer reps) {
    if (reps == 1) {
      return Optional.of(weight);
    }

    if (reps > 1 && reps <= 10) {
      return Optional.of(Math.round(weight * (1 + reps.doubleValue() / 30)));
    }

    return Optional.empty();
  }
}
