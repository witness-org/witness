package com.witness.server.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.exercise.UserExercise;
import com.witness.server.entity.user.User;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.repository.UserRepository;
import com.witness.server.util.Comparators;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;

class ExerciseRepositoryTest extends BaseRepositoryTest {
  private static final String DATA_ROOT = "data/unit/repository/exercise-repository-test/";
  private static final Comparator<Exercise> EXERCISE_COMPARATOR = Comparators.getEntityComparator(Exercise.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ExerciseRepository exerciseRepository;

  @Autowired
  private UserExerciseRepository userExerciseRepository;

  @BeforeEach
  public void setup() {
    setSequenceValues(1, "user_id_sequence", "exercise_id_sequence");
    userRepository.deleteAll();
    exerciseRepository.deleteAll();
    userExerciseRepository.deleteAll();
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "findAllForUser.json", type = FindAllForUserTestSpecification[].class)
  })
  void findAllForUser(FindAllForUserTestSpecification specification) {
    userRepository.saveAllAndFlush(specification.users);
    exerciseRepository.saveAllAndFlush(specification.exercises);
    userExerciseRepository.saveAllAndFlush(specification.userExercises);

    var result = exerciseRepository.findAllForUser(specification.queryUser, specification.queryMuscleGroup);
    var partition = result.stream().collect(Collectors.partitioningBy(exercise -> exercise instanceof UserExercise));
    var userExercises = partition.get(true).stream().map(exercise -> (UserExercise) exercise).collect(Collectors.toList());
    var exercises = partition.get(false);

    assertThat(exercises)
        .usingElementComparator(EXERCISE_COMPARATOR)
        .containsExactlyInAnyOrderElementsOf(specification.expectedExercises);
    assertThat(userExercises)
        .usingElementComparator(EXERCISE_COMPARATOR)
        .containsExactlyInAnyOrderElementsOf(specification.expectedUserExercises);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "findAllByUser.json", type = FindAllByUserTestSpecification[].class)
  })
  void findAllByUser(FindAllByUserTestSpecification specification) {
    userRepository.saveAllAndFlush(specification.users);
    exerciseRepository.saveAllAndFlush(specification.exercises);
    userExerciseRepository.saveAllAndFlush(specification.userExercises);

    var result = exerciseRepository.findAllByUser(specification.queryUser);
    var partition = result.stream().collect(Collectors.partitioningBy(exercise -> exercise instanceof UserExercise));
    var userExercises = partition.get(true).stream().map(exercise -> (UserExercise) exercise).collect(Collectors.toList());
    var exercises = partition.get(false);

    assertThat(exercises).isEmpty();
    assertThat(userExercises)
        .usingElementComparator(EXERCISE_COMPARATOR)
        .containsExactlyInAnyOrderElementsOf(specification.expectedUserExercises);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "existsByName.json", type = ExistsByNameTestSpecification[].class)
  })
  void existsByName(ExistsByNameTestSpecification specification) {
    exerciseRepository.saveAllAndFlush(specification.exercises);

    assertThat(exerciseRepository.existsByName(specification.queryName)).isEqualTo(specification.expectedResult);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "findById_exerciseRepository.json", type = FindByIdTestSpecification[].class)
  })
  void findById_exerciseRepository(FindByIdTestSpecification specification) {
    genericFindByIdTest(specification, () -> exerciseRepository.findById(specification.id));
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "findById_userExerciseRepository.json", type = FindByIdTestSpecification[].class)
  })
  void findById_userExerciseRepository(FindByIdTestSpecification specification) {
    genericFindByIdTest(specification, () -> userExerciseRepository.findById(specification.id));
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "findInitialExerciseById.json", type = FindByIdTestSpecification[].class)
  })
  void findInitialExerciseById(FindByIdTestSpecification specification) {
    genericFindByIdTest(specification, () -> exerciseRepository.findInitialExerciseById(specification.id));

  }

  private <T extends Exercise> void genericFindByIdTest(FindByIdTestSpecification specification, Supplier<Optional<T>> lookup) {
    userRepository.saveAndFlush(specification.user);
    exerciseRepository.saveAllAndFlush(specification.exercises);
    userExerciseRepository.saveAllAndFlush(specification.userExercises);

    var findResult = lookup.get();
    if (specification.expectsResult) {
      assertThat(findResult).isNotEmpty();
    } else {
      assertThat(findResult).isEmpty();
    }
  }

  @Data
  @NoArgsConstructor
  static class ExistsByNameTestSpecification {
    private List<Exercise> exercises;
    private String queryName;
    private boolean expectedResult;
  }

  @Data
  @NoArgsConstructor
  static class FindAllForUserTestSpecification {
    private List<User> users;
    private List<Exercise> exercises;
    private List<UserExercise> userExercises;
    private User queryUser;
    private MuscleGroup queryMuscleGroup;
    private List<Exercise> expectedExercises;
    private List<UserExercise> expectedUserExercises;
  }

  @Data
  @NoArgsConstructor
  static class FindAllByUserTestSpecification {
    private List<User> users;
    private List<Exercise> exercises;
    private List<UserExercise> userExercises;
    private User queryUser;
    private List<UserExercise> expectedUserExercises;
  }

  @Data
  @NoArgsConstructor
  static class FindByIdTestSpecification {
    private User user;
    private List<Exercise> exercises;
    private List<UserExercise> userExercises;
    private Long id;
    private Boolean expectsResult;
  }
}

