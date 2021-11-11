package com.witness.server.service.impl;

import com.google.common.collect.Iterables;
import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.ExerciseReference;
import com.witness.server.entity.workout.Set;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.repository.ExerciseLogRepository;
import com.witness.server.repository.SetLogRepository;
import com.witness.server.repository.WorkoutLogRepository;
import com.witness.server.service.EntityAccessor;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.TimeService;
import com.witness.server.service.UserService;
import com.witness.server.service.WorkoutLogService;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(rollbackFor = Throwable.class)
public class WorkoutLogServiceImpl implements WorkoutLogService, EntityAccessor {
  // TODO validate positions upon create
  // TODO forbid position change in other requests then explicit updatePositions
  private final ExerciseService exerciseService;
  private final WorkoutLogRepository workoutLogRepository;
  private final ExerciseLogRepository exerciseLogRepository;
  private final SetLogRepository setLogRepository;
  private final UserService userService;
  private final TimeService timeService;

  @Autowired
  public WorkoutLogServiceImpl(ExerciseService exerciseService, WorkoutLogRepository workoutLogRepository, TimeService timeService,
                               ExerciseLogRepository exerciseLogRepository, SetLogRepository setLogRepository, UserService userService) {
    this.exerciseService = exerciseService;
    this.workoutLogRepository = workoutLogRepository;
    this.exerciseLogRepository = exerciseLogRepository;
    this.setLogRepository = setLogRepository;
    this.userService = userService;
    this.timeService = timeService;
  }

  @Override
  public List<WorkoutLog> getWorkoutLogsOfDay(String firebaseId, ZonedDateTime date) {
    var startOfDay = date.with(LocalTime.MIN);
    var endOfDay = date.with(LocalTime.MAX);
    return workoutLogRepository.findByLoggedOnBetweenAndUserFirebaseIdEquals(startOfDay, endOfDay, firebaseId);
  }

  @Override
  public WorkoutLog createWorkoutLog(WorkoutLog workoutLog, String firebaseId) throws DataAccessException, InvalidRequestException {
    log.info("Creating new workout for user with Firebase ID {}", firebaseId);

    var user = getUser(userService, firebaseId);
    var workoutLogToPersist = workoutLog
        .toBuilder()
        .exerciseLogs(new ArrayList<>()) // persist with empty list, add ExerciseLogs only later on to establish bidirectional relationships
        .user(user)
        .loggedOn(timeService.getCurrentTime())
        .build();

    var persistedWorkoutLog = workoutLogRepository.save(workoutLogToPersist);
    for (var exerciseLog : workoutLog.getExerciseLogs()) {
      persistedWorkoutLog = addExerciseLogToWorkoutLog(persistedWorkoutLog, exerciseLog);
    }

    return persistedWorkoutLog;
  }

  @Override
  public WorkoutLog setWorkoutDuration(String firebaseId, Long workoutLogId, Integer duration) throws DataAccessException, InvalidRequestException {
    log.info("Setting workout duration for workout with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    workoutLog.setDurationMinutes(duration);
    return workoutLogRepository.save(workoutLog);
  }

  @Override
  public void deleteWorkoutLog(String firebaseId, Long workoutLogId) throws DataAccessException, InvalidRequestException {
    log.info("Deleting workout with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    workoutLogRepository.delete(workoutLog);
  }

  @Override
  public WorkoutLog addExerciseLog(String firebaseId, Long workoutLogId, ExerciseLog exerciseLog) throws DataAccessException,
      InvalidRequestException {
    log.info("Adding exercise log to workout log with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    workoutLog = addExerciseLogToWorkoutLog(workoutLog, exerciseLog);

    return workoutLogRepository.save(workoutLog);
  }

  @Override
  public WorkoutLog updateExerciseLogPositions(String firebaseId, Long workoutLogId, Map<Long, Integer> newPositions)
      throws DataAccessException, InvalidRequestException {
    log.info("Updating the positions of exercise logs in workout log with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    return updateExerciseLogPositions(workoutLog, newPositions);
  }

  @Override
  public WorkoutLog deleteExerciseLog(String firebaseId, Long workoutLogId, Long exerciseLogId) throws DataAccessException, InvalidRequestException {
    log.info("Deleting exercise log with ID {} from workout with ID {}", exerciseLogId, workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfLoggedExerciseNotInWorkoutLog(exerciseLog, workoutLog);

    if (!workoutLog.removeExerciseLog(exerciseLog)) {
      log.error("Failed to remove exercise log with ID {} from workout log with ID {}", exerciseLogId, workoutLogId);
      throw new DataAccessException("An error occurred while removing the requested exercise log.");
    }

    // after removal of exercise log, fill gaps in positions (from {a->1, b->2, c->3, d->4} to {a->1, c->3, d->4} to {a->1, c->2, d->3})
    var currentPositions = getExerciseLogPositions(workoutLog);
    updateExerciseLogPositions(workoutLog, currentPositions);

    return workoutLogRepository.save(workoutLog);
  }

  @Override
  public WorkoutLog setExerciseLogComment(String firebaseId, Long workoutLogId, Long exerciseLogId, String comment)
      throws DataAccessException, InvalidRequestException {
    log.info("Setting the comment of exercise log with ID {} form workout with ID {}", exerciseLogId, workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfLoggedExerciseNotInWorkoutLog(exerciseLog, workoutLog);

    exerciseLog.setComment(comment);

    return workoutLogRepository.save(workoutLog);
  }

  @Override
  public WorkoutLog addSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, SetLog setLog)
      throws DataAccessException, InvalidRequestException {
    log.info("Adding set log to exercise log with ID {}", exerciseLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfLoggedExerciseNotInWorkoutLog(exerciseLog, workoutLog);

    setLog.setPosition(exerciseLog.getSetLogs().size() + 1);
    addSetLogToExerciseLog(exerciseLog, setLog);

    return workoutLog;
  }

  @Override
  public WorkoutLog updateSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, SetLog setLog)
      throws DataAccessException, InvalidRequestException {
    var setLogId = setLog.getId();
    log.info("Editing set log with ID {}", setLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfLoggedExerciseNotInWorkoutLog(exerciseLog, workoutLog);

    var setLogToUpdate = getSetLogOrThrow(setLogId);
    throwIfLoggedSetNotInExerciseLog(setLogToUpdate, exerciseLog);

    if (!Objects.equals(setLogToUpdate.getPosition(), setLog.getPosition())) {
      log.error("Position of set log must not be changed during update.");
      throw new InvalidRequestException("Position of set log may only be changed via the designated endpoint operation.");
    }

    validateLoggingType(exerciseLog.getExercise(), setLog);
    setLog.setExerciseLog(exerciseLog);
    setLogRepository.save(setLog);

    return workoutLog;
  }

  @Override
  public WorkoutLog updateSetLogPositions(String firebaseId, Long workoutLogId, Long exerciseLogId, Map<Long, Integer> newPositions)
      throws DataAccessException, InvalidRequestException {
    log.info("Updating the positions of set logs in exercise log with ID {}", exerciseLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfLoggedExerciseNotInWorkoutLog(exerciseLog, workoutLog);

    return updateSetLogPositions(workoutLog, exerciseLog, newPositions);
  }

  @Override
  public WorkoutLog deleteSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, Long setLogId)
      throws DataAccessException, InvalidRequestException {
    log.info("Deleting set log with ID {} from exercise log with ID {}", setLogId, exerciseLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfLoggedExerciseNotInWorkoutLog(exerciseLog, workoutLog);

    var setLog = getSetLogOrThrow(setLogId);
    throwIfLoggedSetNotInExerciseLog(setLog, exerciseLog);

    if (!exerciseLog.removeSetLog(setLog)) {
      log.error("Failed to remove set log with ID {} from exercise log with ID {}", setLogId, exerciseLogId);
      throw new DataAccessException("An error occurred while removing the requested set log.");
    }

    // after removal of set log, fill gaps in positions (from {a->1, b->2, c->3, d->4} to {a->1, c->3, d->4} to {a->1, c->2, d->3})
    var currentPositions = getSetLogPositions(exerciseLog);
    updateSetLogPositions(workoutLog, exerciseLog, currentPositions);

    exerciseLogRepository.save(exerciseLog);
    return workoutLog;
  }

  private WorkoutLog getWorkoutLogOrThrow(Long id) throws DataNotFoundException {
    return workoutLogRepository
        .findById(id)
        .orElseThrow(() -> new DataNotFoundException("Requested workout log does not exist."));
  }

  private ExerciseLog getExerciseLogOrThrow(Long id) throws DataNotFoundException {
    return exerciseLogRepository
        .findById(id)
        .orElseThrow(() -> new DataNotFoundException("Requested exercise log does not exist."));
  }

  private SetLog getSetLogOrThrow(Long id) throws DataNotFoundException {
    return setLogRepository
        .findById(id)
        .orElseThrow(() -> new DataNotFoundException("Requested set log does not exist."));
  }

  private void throwIfLoggedWorkoutNotByUser(String firebaseId, WorkoutLog workoutLog) throws InvalidRequestException, DataAccessException {
    var user = getUser(userService, firebaseId);

    if (!Role.ADMIN.equals(user.getRole()) && !workoutLog.getUser().equals(user)) {
      log.error("Requested workout was not logged by user with provided Firebase ID {}.", firebaseId);
      throw new InvalidRequestException("The requested workout was not logged by the provided user.");
    }
  }

  private void throwIfLoggedExerciseNotInWorkoutLog(ExerciseLog exerciseLog, WorkoutLog workoutLog)
      throws InvalidRequestException, DataAccessException {
    if (!workoutLog.getExerciseLogs().contains(exerciseLog)) {
      log.error("Requested exercise log with ID {} is not part of requested workout log with ID {}.", exerciseLog.getId(), workoutLog.getId());
      throw new InvalidRequestException("The requested exercise is not part of the requested workout.");
    }

    if (!exerciseLog.getWorkoutLog().equals(workoutLog)) {
      log.error("Requested exercise log with ID {} not consistent with requested workout log with ID {}.", exerciseLog.getId(), workoutLog.getId());
      throw new DataAccessException("There are some data inconsistencies.");
    }
  }

  private void throwIfLoggedSetNotInExerciseLog(SetLog setLog, ExerciseLog exerciseLog) throws InvalidRequestException, DataAccessException {
    if (!exerciseLog.getSetLogs().contains(setLog)) {
      log.error("Requested set log with ID {} is not part of requested exercise log with ID {}.", setLog.getId(), exerciseLog.getId());
      throw new InvalidRequestException("The requested set is not part of the requested exercise.");
    }

    if (!setLog.getExerciseLog().equals(exerciseLog)) {
      log.error("Requested set log with ID {} not consistent with requested exercise log with ID {}.", setLog.getId(), exerciseLog.getId());
      throw new DataAccessException("There are some data inconsistencies.");
    }
  }

  private WorkoutLog addExerciseLogToWorkoutLog(WorkoutLog workoutLog, ExerciseLog newExerciseLog)
      throws DataNotFoundException, InvalidRequestException {
    var exercise = getExercise(exerciseService, newExerciseLog.getExercise().getId());
    var exerciseLogToPersist = newExerciseLog
        .toBuilder()
        .exercise(exercise)
        .setLogs(new ArrayList<>()) // persist with empty list, add SetLogs only later on to establish bidirectional relationships
        .build();

    workoutLog.addExerciseLog(exerciseLogToPersist);
    var workoutLogWithNewExerciseLog = workoutLogRepository.save(workoutLog);

    var persistedExerciseLog = Iterables.getLast(workoutLogWithNewExerciseLog.getExerciseLogs());
    for (var setLog : newExerciseLog.getSetLogs()) {
      addSetLogToExerciseLog(persistedExerciseLog, setLog);
    }

    return workoutLogWithNewExerciseLog;
  }

  private void addSetLogToExerciseLog(ExerciseLog exerciseLog, SetLog setLog) throws InvalidRequestException {
    validateLoggingType(exerciseLog.getExercise(), setLog);
    exerciseLog.addSetLog(setLog);
    exerciseLogRepository.save(exerciseLog);
  }

  private void validateLoggingType(Exercise exercise, SetLog setLog) throws InvalidRequestException {
    var loggingTypes = exercise.getLoggingTypes();
    var setLogType = LoggingType.fromSetLog(setLog);
    if (!loggingTypes.contains(setLogType)) {
      log.error("Logging type {} is not valid for exercise with ID {}.", setLog.getClass().getSimpleName(), exercise.getId());
      throw new InvalidRequestException("Requested logging type is not valid for the specified exercise.");
    }
  }

  private void updateLogPositions(Supplier<Map<Long, Integer>> currentPositionSupplier, Map<Long, Integer> newPositions,
                                  String invalidSpecificationErrorMessage, String duplicateSpecificationErrorMessage,
                                  Consumer<Map<Long, Integer>> newPositionsApplicator) throws InvalidRequestException {
    var currentPositions = currentPositionSupplier.get();

    if (!newPositions.keySet().equals(currentPositions.keySet())) {
      log.error(invalidSpecificationErrorMessage);
      throw new InvalidRequestException(invalidSpecificationErrorMessage);
    }

    if (!newPositions.values().stream().allMatch(new HashSet<>()::add)) {
      log.error(duplicateSpecificationErrorMessage);
      throw new InvalidRequestException(duplicateSpecificationErrorMessage);
    }

    var newPositionsWithoutGaps = simplifyPositionSpecification(newPositions);
    newPositionsApplicator.accept(newPositionsWithoutGaps);
  }

  private WorkoutLog updateExerciseLogPositions(WorkoutLog workoutLog, Map<Long, Integer> newPositions) throws InvalidRequestException {
    updateLogPositions(() -> getExerciseLogPositions(workoutLog),
        newPositions,
        "The map of new exercise log positions must exactly cover the exercise logs of the given workout log.",
        "The assignment of exercise logs to new positions must be unique.",
        positions -> workoutLog.getExerciseLogs().forEach(log1 -> log1.setPosition(positions.get(log1.getId()))));

    return workoutLogRepository.save(workoutLog);
  }

  private WorkoutLog updateSetLogPositions(WorkoutLog workoutLog, ExerciseLog exerciseLog, Map<Long, Integer> newPositions)
      throws InvalidRequestException {
    updateLogPositions(() -> getSetLogPositions(exerciseLog),
        newPositions,
        "The map of new set log positions must exactly cover the set logs of the given exercise log.",
        "The assignment of set logs to new positions must be unique.",
        positionsWithoutGaps -> exerciseLog.getSetLogs().forEach(setLog -> setLog.setPosition(positionsWithoutGaps.get(setLog.getId()))));

    return workoutLogRepository.save(workoutLog);
  }

  private Map<Long, Integer> getExerciseLogPositions(WorkoutLog workoutLog) {
    return workoutLog
        .getExerciseLogs().stream()
        .collect(Collectors.toMap(ExerciseLog::getId, ExerciseReference::getPosition));
  }

  private Map<Long, Integer> getSetLogPositions(ExerciseLog exerciseLog) {
    return exerciseLog
        .getSetLogs().stream()
        .collect(Collectors.toMap(SetLog::getId, Set::getPosition));
  }

  /**
   * <p>
   * Simplifies a position specification by removing gaps in the integers denoting positions of items (logs). Example:
   * </p>
   * <pre>
   *    {2->2, 5->66, 23->34, 7->23, 55->1, 13->7}
   * => {2->2, 5->6,  23->5,  7->4,  55->1, 13->3}
   * </pre>
   * <p>
   * <b>Note:</b> If two (ID) keys map to the same (position) value, their resulting relative order is undefined.
   * </p>
   *
   * @param positions a map that relates items/logs (or their IDs) with positions
   * @return a map that has the same keys as {@code positions}, but the value set is modified such that it represents a gapless integer sequence from
   *     {@code 1} to {@code n} where {@code n} is the number of entries in {@code positions}.
   */
  private Map<Long, Integer> simplifyPositionSpecification(Map<Long, Integer> positions) {
    var sortedEntries = positions.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .collect(Collectors.toList());

    return IntStream.range(0, sortedEntries.size())
        .boxed()
        .collect(Collectors.toMap(i -> sortedEntries.get(i).getKey(), i -> i + 1));
  }
}
