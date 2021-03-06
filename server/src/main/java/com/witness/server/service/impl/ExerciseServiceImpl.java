package com.witness.server.service.impl;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.exercise.UserExercise;
import com.witness.server.entity.user.User;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.RepsSetLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.TimeSetLog;
import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.ServerError;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.ExerciseMapper;
import com.witness.server.model.ExerciseStatistics;
import com.witness.server.repository.ExerciseLogRepository;
import com.witness.server.repository.ExerciseRepository;
import com.witness.server.repository.SetLogRepository;
import com.witness.server.repository.UserExerciseRepository;
import com.witness.server.service.EntityAccessor;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.ExerciseStatisticsCalculationService;
import com.witness.server.service.UserService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ExerciseServiceImpl implements ExerciseService, EntityAccessor {

  private final ExerciseRepository exerciseRepository;
  private final UserExerciseRepository userExerciseRepository;
  private final ExerciseLogRepository exerciseLogRepository;
  private final SetLogRepository setLogRepository;
  private final UserService userService;
  private final ExerciseStatisticsCalculationService exerciseStatisticsCalculationService;
  private final ExerciseMapper exerciseMapper;

  @Autowired
  public ExerciseServiceImpl(ExerciseRepository exerciseRepository, UserExerciseRepository userExerciseRepository,
                             ExerciseLogRepository exerciseLogRepository, SetLogRepository setLogRepository, UserService userService,
                             ExerciseStatisticsCalculationService exerciseStatisticsCalculationService, ExerciseMapper exerciseMapper) {
    this.exerciseRepository = exerciseRepository;
    this.exerciseLogRepository = exerciseLogRepository;
    this.userExerciseRepository = userExerciseRepository;
    this.setLogRepository = setLogRepository;
    this.userService = userService;
    this.exerciseStatisticsCalculationService = exerciseStatisticsCalculationService;
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
    throwIfUserExerciseWithNameExistsForUser(exerciseName, user);

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

    var currentUser = getUser(userService, firebaseId);
    throwIfUserExerciseNotCreatedByUserAndNotAdmin(exerciseToUpdate, currentUser);

    var newName = exercise.getName();
    if (!exerciseToUpdate.getName().equals(newName)) {
      throwIfInitialExerciseWithNameExists(newName);
      throwIfUserExerciseWithNameExistsForUser(newName, currentUser);
    }

    var userExercise = exerciseMapper.fromExerciseAndCreatedBy(exercise, currentUser);
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
        .orElseThrow(() -> new DataNotFoundException("Requested exercise does not exist.", ServerError.EXERCISE_NOT_FOUND));
  }

  @Override
  public Exercise getInitialExerciseById(Long initialExerciseId) throws DataNotFoundException {
    return exerciseRepository
        .findInitialExerciseById(initialExerciseId)
        .orElseThrow(() -> new DataNotFoundException("Requested exercise does not exist", ServerError.EXERCISE_NOT_FOUND));
  }

  @Override
  public UserExercise getUserExerciseById(Long userExerciseId) throws DataNotFoundException {
    return userExerciseRepository
        .findById(userExerciseId)
        .orElseThrow(() -> new DataNotFoundException("Requested exercise does not exist.", ServerError.EXERCISE_NOT_FOUND));
  }

  @Override
  public void deleteInitialExercise(Long initialExerciseId) throws DataNotFoundException {
    log.info("Deleting initial exercise with ID {}", initialExerciseId);

    var initialExercise = getInitialExerciseById(initialExerciseId);
    exerciseRepository.delete(initialExercise);
  }

  @Override
  public void deleteUserExercise(String firebaseId, Long userExerciseId) throws DataAccessException, InvalidRequestException {
    log.info("Deleting user exercise with ID {}", userExerciseId);

    var currentUser = getUser(userService, firebaseId);
    var userExercise = getUserExerciseById(userExerciseId);
    throwIfUserExerciseNotCreatedByUserAndNotAdmin(userExercise, currentUser);
    userExerciseRepository.delete(userExercise);
  }

  private void throwIfInitialExerciseWithNameExists(String name) throws InvalidRequestException {
    if (exerciseRepository.existsByName(name)) {
      log.error("There already exists an initial exercise with the name \"{}\".", name);
      throw new InvalidRequestException("There already exists an initial exercise with this name.", ServerError.INITIAL_EXERCISE_EXISTS);
    }
  }

  private void throwIfUserExerciseWithNameExistsForUser(String name, User user) throws InvalidRequestException {
    if (userExerciseRepository.existsByNameAndCreatedBy(name, user)) {
      log.error("There already exists a user exercise with the name \"{}\" created by the provided user with ID {}.", name, user.getId());
      throw new InvalidRequestException("There already exists an exercise created by the provided user with this name.",
          ServerError.USER_EXERCISE_EXISTS);
    }
  }

  @Override
  public List<ExerciseLog> getExerciseLogs(String firebaseId, Long exerciseId) throws DataAccessException {
    log.info("Retrieving exercise history of exercise with ID {} for user {}", exerciseId, firebaseId);
    var user = getUser(userService, firebaseId);
    var exercise = getExerciseById(exerciseId);
    return exerciseLogRepository.findExerciseLogsByExerciseIdAndUserId(exercise.getId(), user.getId());
  }

  @Override
  public ExerciseStatistics getExerciseStatistics(String firebaseId, Long exerciseId) throws DataAccessException {
    var user = getUser(userService, firebaseId);
    var exercise = getExerciseById(exerciseId);

    var setLogs = setLogRepository.findSetLogsByExerciseLogExerciseIdEqualsAndExerciseLogWorkoutLogUserIdEquals(exercise.getId(), user.getId());
    var subClassLists = getSubClassSetLogStreams(setLogs);
    var repsSetLogs = subClassLists.getFirst();
    var timeSetLogs = subClassLists.getSecond();

    var maxWeightG = setLogs.stream().map(SetLog::getWeightG).max(Comparator.naturalOrder()).orElse(0L);
    var statisticsBuilder = ExerciseStatistics.builder().exercise(exercise).maxWeightG(maxWeightG);

    var loggingTypes = exercise.getLoggingTypes();
    if (loggingTypes.contains(LoggingType.REPS)) {
      var maxReps = repsSetLogs.stream().map(RepsSetLog::getReps).max(Comparator.naturalOrder()).orElse(0);
      var estimatedOneRepMaxG =
          repsSetLogs.stream().map(log -> exerciseStatisticsCalculationService.getEstimatedOneRepMax(log.getWeightG(), log.getReps()))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .max(Comparator.naturalOrder())
              .orElse(null);
      statisticsBuilder.maxReps(maxReps).estimatedOneRepMaxG(estimatedOneRepMaxG);
    }

    if (loggingTypes.contains(LoggingType.TIME)) {
      var maxSeconds = timeSetLogs.stream().map(TimeSetLog::getSeconds).max(Comparator.naturalOrder()).orElse(0);
      statisticsBuilder.maxSeconds(maxSeconds);
    }

    return statisticsBuilder.build();
  }

  private void throwIfUserExerciseNotCreatedByUserAndNotAdmin(UserExercise affectedUserExercise, User user) throws InvalidRequestException {
    if (!Role.ADMIN.equals(user.getRole()) && !affectedUserExercise.getCreatedBy().equals(user)) {
      log.error("Requested exercise was not created by user with provided Firebase ID {}.", user.getFirebaseId());
      throw new InvalidRequestException("The requested exercise was not created by the provided user.",
          ServerError.USER_EXERCISE_NOT_CREATED_BY_USER);
    }
  }

  private Pair<List<RepsSetLog>, List<TimeSetLog>> getSubClassSetLogStreams(List<SetLog> setLogs) {
    var repsSetLogs = new ArrayList<RepsSetLog>();
    var timeSetLogs = new ArrayList<TimeSetLog>();

    for (var setLog : setLogs) {
      if (setLog instanceof RepsSetLog) {
        repsSetLogs.add((RepsSetLog) setLog);
      } else if (setLog instanceof TimeSetLog) {
        timeSetLogs.add((TimeSetLog) setLog);
      }
    }

    return Pair.of(repsSetLogs, timeSetLogs);
  }
}
