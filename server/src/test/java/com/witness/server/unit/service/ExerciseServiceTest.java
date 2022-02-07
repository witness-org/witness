package com.witness.server.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.exercise.UserExercise;
import com.witness.server.entity.user.User;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.ExerciseMapperImpl;
import com.witness.server.repository.ExerciseLogRepository;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.UserService;
import com.witness.server.service.impl.ExerciseServiceImpl;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ExerciseServiceImpl.class, ExerciseMapperImpl.class})
class ExerciseServiceTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/service/exercise-service-test/";

  @Autowired
  private ExerciseService target;

  @MockBean
  private ExerciseRepository exerciseRepository;

  @MockBean
  private UserExerciseRepository userExerciseRepository;

  @MockBean
  private ExerciseLogRepository exerciseLogRepository;

  @MockBean
  private UserService userService;

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1NullId.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class)
  })
  void createInitialExercise_exercise_returnCorrectExercise(Exercise input, Exercise output) throws InvalidRequestException {
    when(exerciseRepository.existsByName(input.getName())).thenReturn(false);
    when(exerciseRepository.save(input)).thenReturn(output);

    assertThat(target.createInitialExercise(input)).isEqualTo(output);

    verify(exerciseRepository, times(1)).existsByName(input.getName());
    verify(exerciseRepository, times(1)).save(any(Exercise.class));
  }

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

    verify(exerciseRepository, times(1)).existsByName(input.getName());
    verify(userExerciseRepository, times(1)).existsByNameAndCreatedBy(input.getName(), user);
    verify(userExerciseRepository, times(1)).save(input);
    verify(userService, times(1)).findByFirebaseId(user.getFirebaseId());
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
      @JsonFileSource(value = DATA_ROOT + "Exercise1_updatedNoNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1_updatedNoNewName.json", type = Exercise.class)
  })
  void updateInitialExercise_updatedExerciseNoNewName_returnCorrectUpdatedExercise(Exercise updated, Exercise existing, Exercise output)
      throws DataNotFoundException, InvalidRequestException {
    when(exerciseRepository.findById(updated.getId())).thenReturn(Optional.of(existing));
    when(exerciseRepository.save(updated)).thenReturn(output);

    assertThat(target.updateInitialExercise(updated)).isEqualTo(output);

    verify(exerciseRepository, times(1)).findById(updated.getId());
    verify(exerciseRepository, never()).existsByName(updated.getName());
    verify(exerciseRepository, times(1)).save(any(Exercise.class));
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1_updatedNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1_updatedNewName.json", type = Exercise.class)
  })
  void updateInitialExercise_updatedExerciseNewName_returnCorrectUpdatedExercise(Exercise updated, Exercise existing, Exercise output)
      throws DataNotFoundException, InvalidRequestException {
    when(exerciseRepository.findById(updated.getId())).thenReturn(Optional.of(existing));
    when(exerciseRepository.existsByName(updated.getName())).thenReturn(false);
    when(exerciseRepository.save(updated)).thenReturn(output);

    assertThat(target.updateInitialExercise(updated)).isEqualTo(output);

    verify(exerciseRepository, times(1)).findById(updated.getId());
    verify(exerciseRepository, times(1)).existsByName(updated.getName());
    verify(exerciseRepository, times(1)).save(any(Exercise.class));
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1_updatedNewName.json", type = Exercise.class)
  })
  void updateInitialExercise_nonExistentExercise_throwException(Exercise updated) {
    when(exerciseRepository.findById(updated.getId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> target.updateInitialExercise(updated)).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1_updatedNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class)
  })
  void updateInitialExercise_updatedExerciseNewExistingName_throwException(Exercise updated, Exercise existing) {
    when(exerciseRepository.findById(updated.getId())).thenReturn(Optional.of(existing));
    when(exerciseRepository.existsByName(updated.getName())).thenReturn(true);

    assertThatThrownBy(() -> target.updateInitialExercise(updated)).isInstanceOf(InvalidRequestException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updateRequestNoNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updatedNoNewName.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updatedNoNewName.json", type = UserExercise.class)
  })
  void updateUserExercise_updatedUserExerciseNoNewName_returnCorrectUpdatedUserExercise(Exercise request, UserExercise updated, User user,
                                                                                        UserExercise existing, UserExercise output)
      throws DataAccessException, InvalidRequestException {
    var firebaseId = user.getFirebaseId();
    when(userExerciseRepository.findById(request.getId())).thenReturn(Optional.of(existing));
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(userExerciseRepository.save(updated)).thenReturn(output);

    assertThat(target.updateUserExercise(firebaseId, request)).isEqualTo(output);

    verify(userExerciseRepository, times(1)).findById(updated.getId());
    verify(exerciseRepository, never()).existsByName(updated.getName());
    verify(userExerciseRepository, never()).existsByNameAndCreatedBy(updated.getName(), user);
    verify(userExerciseRepository, times(1)).save(any(UserExercise.class));
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updateRequestNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updatedNewName.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updatedNewName.json", type = UserExercise.class)
  })
  void updateUserExercise_updatedExerciseNewName_returnCorrectUpdatedUserExercise(Exercise request, UserExercise updated, User user,
                                                                                  UserExercise existing, UserExercise output)
      throws DataAccessException, InvalidRequestException {
    var firebaseId = user.getFirebaseId();
    when(userExerciseRepository.findById(request.getId())).thenReturn(Optional.of(existing));
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(exerciseRepository.existsByName(request.getName())).thenReturn(false);
    when(userExerciseRepository.existsByNameAndCreatedBy(request.getName(), user)).thenReturn(false);
    when(userExerciseRepository.save(updated)).thenReturn(output);

    assertThat(target.updateUserExercise(firebaseId, request)).isEqualTo(output);

    verify(userExerciseRepository, times(1)).findById(updated.getId());
    verify(exerciseRepository, times(1)).existsByName(updated.getName());
    verify(userExerciseRepository, times(1)).existsByNameAndCreatedBy(updated.getName(), user);
    verify(userExerciseRepository, times(1)).save(any(UserExercise.class));
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updateRequestNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class)
  })
  void updateUserExercise_nonExistentExercise_throwException(Exercise updated, User user) {
    when(userExerciseRepository.findById(updated.getId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> target.updateUserExercise(user.getFirebaseId(), updated)).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updateRequestNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "User2.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class)
  })
  void updateUserExercise_wrongUser_throwException(Exercise updated, User user, UserExercise existing) throws DataAccessException {
    var firebaseId = user.getFirebaseId();
    when(userExerciseRepository.findById(updated.getId())).thenReturn(Optional.of(existing));
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);

    assertThatThrownBy(() -> target.updateUserExercise(firebaseId, updated)).isInstanceOf(InvalidRequestException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updateRequestNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class)
  })
  void updateUserExercise_updatedUserExerciseNewExistingNameInitial_throwException(Exercise updated, User user, UserExercise existing)
      throws DataAccessException {
    var firebaseId = user.getFirebaseId();
    when(userExerciseRepository.findById(updated.getId())).thenReturn(Optional.of(existing));
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(exerciseRepository.existsByName(updated.getName())).thenReturn(true);

    assertThatThrownBy(() -> target.updateUserExercise(firebaseId, updated)).isInstanceOf(InvalidRequestException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1_updateRequestNewName.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class)
  })
  void updateUserExercise_updatedUserExerciseNewExistingNameUser_throwException(Exercise updated, User user, UserExercise existing)
      throws DataAccessException {
    var firebaseId = user.getFirebaseId();
    when(userExerciseRepository.findById(updated.getId())).thenReturn(Optional.of(existing));
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(exerciseRepository.existsByName(updated.getName())).thenReturn(false);
    when(userExerciseRepository.existsByNameAndCreatedBy(updated.getName(), user)).thenReturn(true);

    assertThatThrownBy(() -> target.updateUserExercise(firebaseId, updated)).isInstanceOf(InvalidRequestException.class);
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

    verify(exerciseRepository, times(1)).findAllForUser(user, muscleGroup);
    verify(userService, times(1)).findByFirebaseId(user.getFirebaseId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
      @JsonFileSource(value = DATA_ROOT + "Exercises_1-2.json", type = Exercise[].class, arrayToList = true)
  })
  void getExercisesCreatedByUser_user_returnList(User user, List<Exercise> exercises) throws DataAccessException {
    var firebaseId = user.getFirebaseId();
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);
    when(exerciseRepository.findAllByUser(user)).thenReturn(exercises);

    assertThat(target.getExercisesCreatedByUser(firebaseId)).containsExactlyInAnyOrderElementsOf(exercises);

    verify(exerciseRepository, times(1)).findAllByUser(user);
    verify(userService, times(1)).findByFirebaseId(user.getFirebaseId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class)
  })
  void getExerciseById_existingId_returnExercise(Exercise persistedExercise) throws DataNotFoundException {
    var exerciseId = persistedExercise.getId();
    when(exerciseRepository.findById(exerciseId)).thenReturn(Optional.of(persistedExercise));

    assertThat(target.getExerciseById(exerciseId)).usingRecursiveComparison().isEqualTo(persistedExercise);
    verify(exerciseRepository, times(1)).findById(exerciseId);
    verifyNoInteractions(userExerciseRepository);
  }

  @Test
  void getExerciseById_nonExistingId_throwException() {
    assertThatThrownBy(() -> target.getExerciseById(-233L)).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class)
  })
  void getUserExerciseById_existingId_returnExercise(UserExercise persistedUserExercise) throws DataNotFoundException {
    var userExerciseId = persistedUserExercise.getId();
    when(userExerciseRepository.findById(userExerciseId)).thenReturn(Optional.of(persistedUserExercise));

    assertThat(target.getUserExerciseById(userExerciseId)).usingRecursiveComparison().isEqualTo(persistedUserExercise);
    verify(userExerciseRepository, times(1)).findById(userExerciseId);
    verifyNoInteractions(exerciseRepository);
  }

  @Test
  void getUserExerciseById_nonExistingId_throwException() {
    assertThatThrownBy(() -> target.getUserExerciseById(-233L)).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class)
  })
  void deleteInitialExercise_existingId_succeeds(Exercise persistedExercise) throws DataNotFoundException {
    var userExerciseId = persistedExercise.getId();
    when(exerciseRepository.findInitialExerciseById(userExerciseId)).thenReturn(Optional.of(persistedExercise));

    target.deleteInitialExercise(userExerciseId);
    verify(exerciseRepository, times(1)).findInitialExerciseById(userExerciseId);
    verify(exerciseRepository, times(1)).delete(persistedExercise);
    verifyNoInteractions(userExerciseRepository);
  }

  @Test
  void deleteInitialExercise_nonExistingId_throwException() {
    assertThatThrownBy(() -> target.deleteInitialExercise(3L)).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise1.json", type = UserExercise.class)
  })
  void deleteInitialExercise_idOfUserExercise_throwException(Exercise persistedExercise, UserExercise persistedUserExercise) {
    when(exerciseRepository.findById(persistedExercise.getId())).thenReturn(Optional.of(persistedExercise));
    when(userExerciseRepository.findById(persistedUserExercise.getId())).thenReturn(Optional.of(persistedUserExercise));

    assertThatThrownBy(() -> target.deleteInitialExercise(persistedUserExercise.getId())).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class),
  })
  void deleteUserExercise_existingId_succeeds(UserExercise persistedUserExercise, User user) throws DataAccessException, InvalidRequestException {
    var userExerciseId = persistedUserExercise.getId();
    var firebaseId = user.getFirebaseId();
    when(userExerciseRepository.findById(userExerciseId)).thenReturn(Optional.of(persistedUserExercise));
    when(userService.findByFirebaseId(firebaseId)).thenReturn(user);

    target.deleteUserExercise(firebaseId, userExerciseId);
    verify(userExerciseRepository, times(1)).findById(userExerciseId);
    verify(userExerciseRepository, times(1)).delete(persistedUserExercise);
    verify(userService, times(1)).findByFirebaseId(firebaseId);
    verifyNoInteractions(exerciseRepository);
  }

  @Test
  void deleteUserExercise_nonExistingId_throwException() {
    assertThatThrownBy(() -> target.deleteUserExercise("irrelevant", 3L)).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class)
  })
  void deleteUserExercise_idOfInitialExercise_throwException(Exercise persistedInitialExercise, UserExercise persistedUserExercise, User user) {
    when(exerciseRepository.findById(persistedInitialExercise.getId())).thenReturn(Optional.of(persistedInitialExercise));
    when(userExerciseRepository.findById(persistedUserExercise.getId())).thenReturn(Optional.of(persistedUserExercise));

    assertThatThrownBy(() -> target.deleteUserExercise(user.getFirebaseId(), persistedInitialExercise.getId()))
        .isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User3.json", type = User.class)
  })
  void deleteUserExercise_userNotCreatorButAdmin_succeeds(UserExercise persistedUserExercise, User user)
      throws DataAccessException, InvalidRequestException {
    when(userExerciseRepository.findById(persistedUserExercise.getId())).thenReturn(Optional.of(persistedUserExercise));
    when(userService.findByFirebaseId(user.getFirebaseId())).thenReturn(user);

    target.deleteUserExercise(user.getFirebaseId(), persistedUserExercise.getId());
    verify(userExerciseRepository, times(1)).findById(persistedUserExercise.getId());
    verify(userExerciseRepository, times(1)).delete(persistedUserExercise);
    verify(userService, times(1)).findByFirebaseId(user.getFirebaseId());
    verifyNoInteractions(exerciseRepository);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserExercise2.json", type = UserExercise.class),
      @JsonFileSource(value = DATA_ROOT + "User4.json", type = User.class)
  })
  void deleteUserExercise_userNeitherCreatorNorAdmin_throwException(UserExercise persistedUserExercise, User user)
      throws DataAccessException {
    when(userExerciseRepository.findById(persistedUserExercise.getId())).thenReturn(Optional.of(persistedUserExercise));
    when(userService.findByFirebaseId(user.getFirebaseId())).thenReturn(user);

    assertThatThrownBy(() -> target.deleteUserExercise(user.getFirebaseId(), persistedUserExercise.getId()))
        .isInstanceOf(InvalidRequestException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "Exercise1.json", type = Exercise.class),
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class)
  })
  void getExerciseLogs_existingExerciseLogsForUser_succeeds(Exercise persistedExercise, User persistedUser) throws DataAccessException {
    when(exerciseRepository.findById(persistedExercise.getId())).thenReturn(Optional.of(persistedExercise));
    when(userService.findByFirebaseId(persistedUser.getFirebaseId())).thenReturn(persistedUser);
    when(exerciseLogRepository.findExerciseLogsByExerciseIdAndUserId(anyLong(), anyLong()))
        .thenReturn(Collections.emptyList());

    var exerciseLogs = target.getExerciseLogs(persistedUser.getFirebaseId(), persistedExercise.getId());
    assertThat(exerciseLogs).isEmpty();
    verify(exerciseRepository, times(1)).findById(persistedExercise.getId());
    verify(exerciseLogRepository, times(1)).findExerciseLogsByExerciseIdAndUserId(persistedExercise.getId(), persistedUser.getId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "User1.json", type = User.class)
  })
  void getExerciseLogs_nonExistingExercise_throwException(User persistedUser) throws DataAccessException {
    when(userService.findByFirebaseId(persistedUser.getFirebaseId())).thenReturn(persistedUser);

    assertThatThrownBy(() -> target.getExerciseLogs("irrelevant", 23L)).isInstanceOf(DataNotFoundException.class);
  }
}
