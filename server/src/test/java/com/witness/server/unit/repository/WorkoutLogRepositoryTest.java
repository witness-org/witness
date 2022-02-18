package com.witness.server.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.user.User;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserRepository;
import com.witness.server.repository.WorkoutLogRepository;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkoutLogRepositoryTest extends BaseRepositoryTest {
  private static final String DATA_ROOT = "data/unit/repository/workout-log-repository-test/";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ExerciseRepository exerciseRepository;

  @Autowired
  private WorkoutLogRepository workoutLogRepository;

  @BeforeEach
  public void setup() {
    setSequenceValues(1, "user_id_sequence", "exercise_id_sequence", "workout_log_id_sequence", "exercise_log_id_sequence");
    userRepository.deleteAll();
    exerciseRepository.deleteAll();
    workoutLogRepository.deleteAll();
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "findNonEmptyByLoggedOnBetweenAndUserFirebaseIdEquals.json",
          type = FindNonEmptyByLoggedOnBetweenAndUserFirebaseIdEqualsSpecification[].class)
  })
  void findNonEmptyByLoggedOnBetweenAndUserFirebaseIdEquals(FindNonEmptyByLoggedOnBetweenAndUserFirebaseIdEqualsSpecification specification) {
    userRepository.saveAllAndFlush(specification.users);
    exerciseRepository.saveAllAndFlush(specification.exercises);
    workoutLogRepository.saveAllAndFlush(specification.workoutLogs);

    var result = workoutLogRepository.findNonEmptyByLoggedOnBetweenAndUserFirebaseIdEquals(specification.startDate, specification.endDate,
        specification.firebaseId);
    var resultIds = result.stream().mapToLong(WorkoutLog::getId);
    assertThat(resultIds)
        .containsExactlyInAnyOrderElementsOf(specification.expectedWorkoutLogIds);
  }

  @Data
  @NoArgsConstructor
  static class FindNonEmptyByLoggedOnBetweenAndUserFirebaseIdEqualsSpecification {
    private List<User> users;
    private List<Exercise> exercises;
    private List<WorkoutLog> workoutLogs;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String firebaseId;
    private List<Long> expectedWorkoutLogIds;
  }
}
