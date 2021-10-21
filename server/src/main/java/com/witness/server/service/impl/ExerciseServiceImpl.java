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
import com.witness.server.service.SecurityService;
import com.witness.server.service.UserService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExerciseServiceImpl implements ExerciseService {

  private final SecurityService securityService;
  private final UserExerciseRepository userExerciseRepository;
  private final ExerciseRepository exerciseRepository;
  private final UserService userService;

  @Autowired
  public ExerciseServiceImpl(SecurityService securityService, UserExerciseRepository userExerciseRepository, ExerciseRepository exerciseRepository,
                             UserService userService) {
    this.securityService = securityService;
    this.userExerciseRepository = userExerciseRepository;
    this.exerciseRepository = exerciseRepository;
    this.userService = userService;
  }


  @Override
  public Exercise createInitialExercise(Exercise exercise) throws InvalidRequestException {
    log.info(String.format("Creating new initial exercise \"%s\".", exercise.getName()));

    if (exerciseRepository.existsByName(exercise.getName())) {
      log.error(String.format("Initial exercise with name \"%s\" already exists.", exercise.getName()));
      throw new InvalidRequestException("There already exists an exercise with this name.");
    }

    return exerciseRepository.save(exercise);
  }

  @Override
  public UserExercise createUserExercise(UserExercise exercise) throws InvalidRequestException, DataAccessException {
    log.info(String.format("Creating new user exercise \"%s\".", exercise.getName()));

    if (exerciseRepository.existsByName(exercise.getName())) {
      log.error(String.format("There already exists an initial exercise with the name \"%s\".", exercise.getName()));
      throw new InvalidRequestException("There already exists an exercise with this name.");
    }

    var user = getLoggedInUser();
    exercise.setCreatedBy(user);

    if (userExerciseRepository.existsByNameAndCreatedBy(exercise.getName(), user)) {
      log.error(String.format("There already exists a user exercise with the name \"%s\" created by the logged-in user.", exercise.getName()));
      throw new InvalidRequestException("You already created an exercise with this name.");
    }

    return userExerciseRepository.save(exercise);
  }

  @Override
  public List<Exercise> getExercisesForUserByMuscleGroup(MuscleGroup muscleGroup) throws DataAccessException {
    var user = getLoggedInUser();

    log.info(String.format("Fetching exercises for muscle group %s for user with ID %d.", muscleGroup, user.getId()));
    return exerciseRepository.findAllForUser(user, muscleGroup);
  }

  @Override
  public List<Exercise> getExercisesCreatedByUser() throws DataAccessException {
    var user = getLoggedInUser();

    log.info(String.format("Fetching exercises created by user with ID %d.", user.getId()));
    return exerciseRepository.findAllByUser(user);
  }

  private User getLoggedInUser() throws DataAccessException {
    var currentUser = securityService.getCurrentUser();
    return userService.findByFirebaseId(currentUser.getUid());
  }
}
