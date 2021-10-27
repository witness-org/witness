package com.witness.server.service.impl;

import com.witness.server.entity.Exercise;
import com.witness.server.entity.User;
import com.witness.server.entity.UserExercise;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExerciseServiceImpl implements ExerciseService {

  private final UserService userService;
  private final ExerciseRepository exerciseRepository;
  private final UserExerciseRepository userExerciseRepository;

  @Autowired
  public ExerciseServiceImpl(UserService userService, ExerciseRepository exerciseRepository, UserExerciseRepository userExerciseRepository) {
    this.userService = userService;
    this.exerciseRepository = exerciseRepository;
    this.userExerciseRepository = userExerciseRepository;
  }

  @Override
  public Exercise createInitialExercise(Exercise exercise) throws InvalidRequestException {
    var exerciseName = exercise.getName();
    log.info("Creating new initial exercise with name \"{}\".", exerciseName);

    throwIfInitialExerciseWithNameExists(exerciseName);

    return exerciseRepository.save(exercise);
  }

  @Override
  public UserExercise createUserExercise(String firebaseId, UserExercise exercise) throws InvalidRequestException, DataAccessException {
    var exerciseName = exercise.getName();
    log.info("Creating new user exercise with name \"{}\".", exerciseName);

    throwIfInitialExerciseWithNameExists(exerciseName);

    var user = getUser(firebaseId);
    throwIfUserExerciseCreatedByWithNameExists(exerciseName, user);

    exercise.setCreatedBy(user);
    return userExerciseRepository.save(exercise);
  }

  @Override
  public Exercise updateInitialExercise(Exercise exercise) throws InvalidRequestException, DataNotFoundException {
    var exerciseId = exercise.getId();
    log.info("Updating initial exercise with ID {}.", exerciseId);

    var exerciseToUpdate = exerciseRepository.findById(exerciseId).orElseThrow(() -> new DataNotFoundException("Requested exercise does not exist."));

    var newName = exercise.getName();
    if (!exerciseToUpdate.getName().equals(newName)) {
      throwIfInitialExerciseWithNameExists(newName);
    }

    return exerciseRepository.save(exercise);
  }

  @Override
  public UserExercise updateUserExercise(String firebaseId, Exercise exercise) throws DataAccessException, InvalidRequestException {
    var exerciseId = exercise.getId();
    log.info("Updating user exercise with ID {}.", exerciseId);

    var exerciseToUpdate = userExerciseRepository
        .findById(exerciseId)
        .orElseThrow(() -> new DataNotFoundException("Requested exercise does not exist."));

    var user = getUser(firebaseId);
    if (!Role.ADMIN.equals(user.getRole()) && !exerciseToUpdate.getCreatedBy().equals(user)) {
      log.error("Requested exercise was not created by user with provided Firebase ID {}.", firebaseId);
      throw new InvalidRequestException("The requested exercise was not created by the provided user.");
    }

    var newName = exercise.getName();
    if (!exerciseToUpdate.getName().equals(newName)) {
      throwIfInitialExerciseWithNameExists(newName);
      throwIfUserExerciseCreatedByWithNameExists(newName, user);
    }

    var userExercise = fromExerciseAndCreatedBy(exercise, user);
    return userExerciseRepository.save(userExercise);
  }

  @Override
  public List<Exercise> getExercisesForUserByMuscleGroup(String firebaseId, MuscleGroup muscleGroup) throws DataAccessException {
    var user = getUser(firebaseId);

    log.info("Fetching exercises for muscle group \"{}\" for user with ID {}.", muscleGroup, user.getId());
    return exerciseRepository.findAllForUser(user, muscleGroup);
  }

  @Override
  public List<Exercise> getExercisesCreatedByUser(String firebaseId) throws DataAccessException {
    var user = getUser(firebaseId);

    log.info("Fetching exercises created by user with ID {}.", user.getId());
    return exerciseRepository.findAllByUser(user);
  }

  private User getUser(String firebaseId) throws DataAccessException {
    return userService.findByFirebaseId(firebaseId);
  }

  private void throwIfInitialExerciseWithNameExists(String name) throws InvalidRequestException {
    if (exerciseRepository.existsByName(name)) {
      log.error("There already exists an initial exercise with the name \"{}\".", name);
      throw new InvalidRequestException("There already exists an initial exercise with this name.");
    }
  }

  private void throwIfUserExerciseCreatedByWithNameExists(String name, User user) throws InvalidRequestException {
    if (userExerciseRepository.existsByNameAndCreatedBy(name, user)) {
      log.error("There already exists a user exercise with the name \"{}\" created by the provided user with ID {}.", name, user.getId());
      throw new InvalidRequestException("There already exists an exercise created by the provided user with this name.");
    }
  }

  private static UserExercise fromExerciseAndCreatedBy(Exercise exercise, User createdBy) {
    return UserExercise.userExerciseBuilder()
        .id(exercise.getId())
        .name(exercise.getName())
        .description(exercise.getDescription())
        .muscleGroups(exercise.getMuscleGroups())
        .loggingTypes(exercise.getLoggingTypes())
        .createdBy(createdBy)
        .build();
  }
}
