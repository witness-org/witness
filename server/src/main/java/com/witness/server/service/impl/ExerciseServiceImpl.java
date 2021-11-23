package com.witness.server.service.impl;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.exercise.UserExercise;
import com.witness.server.entity.user.User;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.ExerciseMapper;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.service.EntityAccessor;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExerciseServiceImpl implements ExerciseService, EntityAccessor {

  private final ExerciseRepository exerciseRepository;
  private final UserExerciseRepository userExerciseRepository;
  private final UserService userService;
  private final ExerciseMapper exerciseMapper;

  @Autowired
  public ExerciseServiceImpl(ExerciseRepository exerciseRepository, UserExerciseRepository userExerciseRepository, UserService userService,
                             ExerciseMapper exerciseMapper) {
    this.exerciseRepository = exerciseRepository;
    this.userExerciseRepository = userExerciseRepository;
    this.userService = userService;
    this.exerciseMapper = exerciseMapper;
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

    var user = getUser(userService, firebaseId);
    throwIfUserExerciseCreatedByWithNameExists(exerciseName, user);

    exercise.setCreatedBy(user);
    return userExerciseRepository.save(exercise);
  }

  @Override
  public Exercise updateInitialExercise(Exercise exercise) throws InvalidRequestException, DataNotFoundException {
    var exerciseId = exercise.getId();
    log.info("Updating initial exercise with ID {}.", exerciseId);

    var exerciseToUpdate = getExerciseById(exerciseId);

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

    var exerciseToUpdate = getUserExerciseById(exerciseId);

    var user = getUser(userService, firebaseId);
    if (!Role.ADMIN.equals(user.getRole()) && !exerciseToUpdate.getCreatedBy().equals(user)) {
      log.error("Requested exercise was not created by user with provided Firebase ID {}.", firebaseId);
      throw new InvalidRequestException("The requested exercise was not created by the provided user.");
    }

    var newName = exercise.getName();
    if (!exerciseToUpdate.getName().equals(newName)) {
      throwIfInitialExerciseWithNameExists(newName);
      throwIfUserExerciseCreatedByWithNameExists(newName, user);
    }

    var userExercise = exerciseMapper.fromExerciseAndCreatedBy(exercise, user);
    return userExerciseRepository.save(userExercise);
  }

  @Override
  public List<Exercise> getExercisesForUserByMuscleGroup(String firebaseId, MuscleGroup muscleGroup) throws DataAccessException {
    var user = getUser(userService, firebaseId);

    log.info("Fetching exercises for muscle group \"{}\" for user with ID {}.", muscleGroup, user.getId());
    return exerciseRepository.findAllForUser(user, muscleGroup);
  }

  @Override
  public List<Exercise> getExercisesCreatedByUser(String firebaseId) throws DataAccessException {
    var user = getUser(userService, firebaseId);

    log.info("Fetching exercises created by user with ID {}.", user.getId());
    return exerciseRepository.findAllByUser(user);
  }

  @Override
  public Exercise getExerciseById(Long exerciseId) throws DataNotFoundException {
    return exerciseRepository
        .findById(exerciseId)
        .orElseThrow(() -> new DataNotFoundException("Requested exercise does not exist."));
  }

  @Override
  public UserExercise getUserExerciseById(Long exerciseId) throws DataNotFoundException {
    return userExerciseRepository
        .findById(exerciseId)
        .orElseThrow(() -> new DataNotFoundException("Requested exercise does not exist."));
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
}
