package com.witness.server.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.witness.server.entity.Exercise;
import com.witness.server.entity.User;
import com.witness.server.entity.UserExercise;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.UserService;
import com.witness.server.service.impl.ExerciseServiceImpl;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = ExerciseServiceImpl.class)
class ExerciseServiceTest extends BaseUnitTest {

  private static final String DATA_ROOT = "data/unit/service/exercise-service-test/";

  @Autowired
  private ExerciseService target;

  @MockBean
  private ExerciseRepository exerciseRepository;

  @MockBean
  private UserExerciseRepository userExerciseRepository;

  @MockBean
  private UserService userService;

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1NullId.json", type = Exercise.class)
  })
  void createInitialExercise_exerciseWithAlreadyTakenName_throwException(Exercise exercise) {
    when(exerciseRepository.existsByName(exercise.getName())).thenReturn(true);

    assertThatThrownBy(() -> target.createInitialExercise(exercise))
        .isInstanceOf(InvalidRequestException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1NullId.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class)
  })
  void testCreateInitialExercise_givenExercise_returnCorrectExercise(Exercise input, Exercise output) throws InvalidRequestException {
    when(exerciseRepository.existsByName(input.getName())).thenReturn(false);
    when(exerciseRepository.save(input)).thenReturn(output);

    assertThat(target.createInitialExercise(input)).isEqualTo(output);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1NullId.json", type = UserExercise.class)
  })
  void createUserExercise_initialExerciseWithAlreadyTakenName_throwException(UserExercise exercise) {
    when(exerciseRepository.existsByName(exercise.getName())).thenReturn(true);

    assertThatThrownBy(() -> target.createUserExercise("firebaseId", exercise))
        .isInstanceOf(InvalidRequestException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1NullId.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class)
  })
  void createUserExercise_createdExerciseWithAlreadyTakenName_throwException(UserExercise exercise, User user)
      throws DataAccessException {
    var firebaseId = user.getFirebaseId();
    when(exerciseRepository.existsByName(exercise.getName())).thenReturn(false);
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(userExerciseRepository.existsByNameAndCreatedBy(exercise.getName(), user)).thenReturn(true);

    assertThatThrownBy(() -> target.createUserExercise(firebaseId, exercise))
        .isInstanceOf(InvalidRequestException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1NullId.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class)
  })
  void createUserExercise_userExercise_returnCorrectUserExercise(UserExercise input, User user, UserExercise output)
      throws DataAccessException, InvalidRequestException {
    var firebaseId = user.getFirebaseId();
    when(exerciseRepository.existsByName(input.getName())).thenReturn(false);
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(userExerciseRepository.existsByNameAndCreatedBy(input.getName(), user)).thenReturn(false);
    when(userExerciseRepository.save(input)).thenReturn(output);

    assertThat(target.createUserExercise(firebaseId, input)).isEqualTo(output);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercises_1-2.json", type = Exercise[].class, arrayToList = true)
  })
  void getExercisesForUserByMuscleGroup_user_returnList(User user, List<Exercise> exercises) throws DataAccessException {
    var firebaseId = user.getFirebaseId();
    var muscleGroup = MuscleGroup.CHEST;

    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(exerciseRepository.findAllForUser(user, muscleGroup)).thenReturn(exercises);

    assertThat(target.getExercisesForUserByMuscleGroup(firebaseId, muscleGroup)).containsExactlyInAnyOrderElementsOf(exercises);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercises_1-2.json", type = Exercise[].class, arrayToList = true)
  })
  void getExercisesCreatedByUser_user_returnList(User user, List<Exercise> exercises) throws DataAccessException {
    String firebaseId = user.getFirebaseId();
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(exerciseRepository.findAllByUser(user)).thenReturn(exercises);

    assertThat(target.getExercisesCreatedByUser(firebaseId)).containsExactlyInAnyOrderElementsOf(exercises);
  }
}
