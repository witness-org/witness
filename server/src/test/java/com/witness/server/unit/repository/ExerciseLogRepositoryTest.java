package com.witness.server.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.user.User;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.repository.ExerciseLogRepository;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.SetLogRepository;
import com.witness.server.repository.UserRepository;
import com.witness.server.repository.WorkoutLogRepository;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;

class ExerciseLogRepositoryTest extends BaseRepositoryTest {
  private static final String DATA_ROOT = "data/unit/repository/exercise-log-repository-test/";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ExerciseRepository exerciseRepository;

  @Autowired
  WorkoutLogRepository workoutLogRepository;

  @Autowired
  ExerciseLogRepository exerciseLogRepository;

  @Autowired
  SetLogRepository setLogRepository;

  @BeforeEach
  public void setup() {
    setSequenceValues(1, "user_id_sequence", "exercise_id_sequence",
        "workout_log_id_sequence", "exercise_log_id_sequence", "set_log_id_sequence");
    userRepository.deleteAll();
    exerciseRepository.deleteAll();
    workoutLogRepository.deleteAll();
    exerciseLogRepository.deleteAll();
    setLogRepository.deleteAll();
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "findExerciseLogsByExerciseIdAndUserId.json",
          type = FindExerciseLogsByExerciseIdAndUserIdTestSpecification[].class)
  })
  void findExerciseLogsByExerciseIdAndUserId(FindExerciseLogsByExerciseIdAndUserIdTestSpecification specification) {
    userRepository.saveAllAndFlush(specification.users);
    exerciseRepository.saveAllAndFlush(specification.exercises);
    workoutLogRepository.saveAllAndFlush(specification.workoutLogs);

    var exerciseLogs = exerciseLogRepository.findExerciseLogsByExerciseIdAndUserId(specification.queriedExerciseId, specification.queriedUserId);

    assertThat(exerciseLogs)
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("workoutLog", "setLogs")
        .containsExactlyInAnyOrderElementsOf(specification.expectedExerciseLogs);
  }

  @Data
  @NoArgsConstructor
  static class FindExerciseLogsByExerciseIdAndUserIdTestSpecification {
    private List<User> users;
    private List<Exercise> exercises;
    private List<WorkoutLog> workoutLogs;
    private Long queriedUserId;
    private Long queriedExerciseId;
    private List<ExerciseLog> expectedExerciseLogs;
  }
}
