package com.witness.server.service.impl;

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
    log.info("Creating new initial exercise with name \"{}\".", exercise.getName());

    if (exerciseRepository.existsByName(exercise.getName())) {
      log.error("There already exists an initial exercise with the name \"{}\".", exercise.getName());
      throw new InvalidRequestException("There already exists an exercise with this name.");
    }

    return exerciseRepository.save(exercise);
  }

  @Override
  public UserExercise createUserExercise(String firebaseId, UserExercise exercise) throws InvalidRequestException, DataAccessException {
    log.info("Creating new user exercise with name \"{}\".", exercise.getName());

    if (exerciseRepository.existsByName(exercise.getName())) {
      log.error("There already exists an initial exercise with the name \"{}\".", exercise.getName());
      throw new InvalidRequestException("There already exists an exercise with this name.");
    }

    var user = getUser(firebaseId);
    exercise.setCreatedBy(user);

    if (userExerciseRepository.existsByNameAndCreatedBy(exercise.getName(), user)) {
      log.error("There already exists a user exercise with the name \"{}\" created by the provided user with ID {}.",
          exercise.getName(), user.getId());
      throw new InvalidRequestException("You already created an exercise with this name.");
    }

    return userExerciseRepository.save(exercise);
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
}
