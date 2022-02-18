package com.witness.server.service;


import java.util.Optional;

/**
 * Provides methods for calculating relevant exercise statistics parameters based on common formulas, e.g. the estimated one-repetition maximum.
 */
public interface ExerciseStatisticsCalculationService {

  /**
   * <p>
   * Calculates the estimated one-repetition maximum (1RM) in grams based on the provided values using the Epley formula
   * (see <a href="https://en.wikipedia.org/wiki/One-repetition_maximum#Epley_formula">the corresponding Wikipedia entry</a>).
   * </p>
   * <p>
   * If the number of reps logged in the {@code setLog} exceeds 10, a reliable estimation would not really be possible. Hence, in this case,
   * {@code Optional.empty()} is returned.
   * </p>
   *
   * @param weight weight in grams
   * @param reps repetitions
   * @return the estimated 1RM or {@code Optional.empty()} if the amount of logged reps does not allow for reliable estimation
   */
  Optional<Long> getEstimatedOneRepMax(Long weight, Integer reps);
}
