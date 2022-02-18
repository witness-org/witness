package com.witness.server.unit.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.witness.server.service.ExerciseStatisticsCalculationService;
import com.witness.server.service.impl.ExerciseStatisticsCalculationServiceImpl;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ExerciseStatisticsCalculationServiceImpl.class})
public class ExerciseStatisticsCalculationServiceTest {
  private static final String DATA_ROOT = "data/unit/service/exercise-statistics-calculation-service-test/";

  @Autowired
  private ExerciseStatisticsCalculationService target;

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "getEstimatedOneRepMax_nonEmptyResult.json",
          type = ExerciseStatisticsCalculationServiceTest.GetEstimatedOneRepMaxSpecification[].class)
  })
  void getEstimatedOneRepMaxFromSetLog_givenSetLog_returnCorrectEstimatedOneRepMax(
      ExerciseStatisticsCalculationServiceTest.GetEstimatedOneRepMaxSpecification specification) {
    var oneRepMax = target.getEstimatedOneRepMax(specification.weightG, specification.reps);
    assertThat(oneRepMax).isPresent();
    assertThat(oneRepMax.get()).isEqualTo(specification.expectedEstimatedOneRepMax);
  }

  @ParameterizedTest
  @CsvSource({"10000,0", "10000,11"})
  void getEstimatedOneRepMaxFromSetLog_givenSetLogWithNoFeasibleValues_returnEmptyOptional(Long weightG, Integer reps) {
    var estimatedOneRepMax = target.getEstimatedOneRepMax(weightG, reps);
    assertThat(estimatedOneRepMax).isEmpty();
  }

  @Data
  @NoArgsConstructor
  private static class GetEstimatedOneRepMaxSpecification {
    Long weightG;
    Integer reps;
    Long expectedEstimatedOneRepMax;
  }
}
