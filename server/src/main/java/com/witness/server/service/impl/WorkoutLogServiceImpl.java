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
import com.witness.server.enumeration.ServerError;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.repository.ExerciseLogRepository;
import com.witness.server.repository.SetLogRepository;
import com.witness.server.repository.WorkoutLogRepository;
import com.witness.server.service.EntityAccessor;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.UserService;
import com.witness.server.service.WorkoutLogService;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
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
  private final ExerciseService exerciseService;
  private final WorkoutLogRepository workoutLogRepository;
  private final ExerciseLogRepository exerciseLogRepository;
  private final SetLogRepository setLogRepository;
  private final UserService userService;

  @Autowired
  public WorkoutLogServiceImpl(ExerciseService exerciseService, WorkoutLogRepository workoutLogRepository,
                               ExerciseLogRepository exerciseLogRepository, SetLogRepository setLogRepository, UserService userService) {
    this.exerciseService = exerciseService;
    this.workoutLogRepository = workoutLogRepository;
    this.exerciseLogRepository = exerciseLogRepository;
    this.setLogRepository = setLogRepository;
    this.userService = userService;
  }

  @Override
  public List<WorkoutLog> getWorkoutLogsOfDay(String firebaseId, ZonedDateTime date) {
    log.info("Getting workout logs of user with Firebase ID {} from day {}", firebaseId, date);

    return getWorkoutLogsLoggedByInPeriod(date, date,
        (start, end) -> workoutLogRepository.findByLoggedOnBetweenAndUserFirebaseIdEquals(start, end, firebaseId));
  }

  @Override
  public Map<ZonedDateTime, List<WorkoutLog>> getNonEmptyWorkoutLogsInPeriod(String firebaseId, ZonedDateTime startDate, ZonedDateTime endDate)
      throws InvalidRequestException {
    log.info("Getting workout logs of user with Firebase ID {} from {} {}", firebaseId, startDate.getMonth(), startDate.getYear());

    if (startDate.isAfter(endDate)) {
      throw new InvalidRequestException("The start date of the logging period must lie before the end date.",
          ServerError.WORKOUT_LOGGING_START_DATE_AFTER_END_DATE);
    }

    var workoutLogs = getWorkoutLogsLoggedByInPeriod(startDate, endDate,
        (start, end) -> workoutLogRepository.findNonEmptyByLoggedOnBetweenAndUserFirebaseIdEquals(start, end, firebaseId));
    return workoutLogs.stream().collect(Collectors.groupingBy(log -> log.getLoggedOn().truncatedTo(ChronoUnit.DAYS)));
  }

  @Override
  public WorkoutLog createWorkoutLog(WorkoutLog workoutLog, String firebaseId) throws DataAccessException, InvalidRequestException {
    log.info("Creating new workout for user with Firebase ID {}", firebaseId);

    var user = getUser(userService, firebaseId);
    var workoutLogToPersist = workoutLog
        .toBuilder()
        .exerciseLogs(new ArrayList<>()) // persist with empty list, add ExerciseLogs only later on to establish bidirectional relationships
        .user(user)
        .build();

    var persistedWorkoutLog = workoutLogRepository.save(workoutLogToPersist);
    for (var exerciseLog : workoutLog.getExerciseLogs()) {
      persistedWorkoutLog = addExerciseLogToWorkoutLog(persistedWorkoutLog, exerciseLog);
    }

    var currentPositions = getExerciseLogPositions(persistedWorkoutLog.getExerciseLogs());
    return updateAndSimplifyExerciseLogPositions(persistedWorkoutLog, currentPositions);
  }

  @Override
  public WorkoutLog setWorkoutDuration(String firebaseId, Long workoutLogId, Integer duration) throws DataAccessException, InvalidRequestException {
    log.info("Setting workout duration for workout with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    workoutLog.setDurationMinutes(duration);
    return workoutLogRepository.save(workoutLog);
  }

  @Override
  public void deleteWorkoutLog(String firebaseId, Long workoutLogId) throws DataAccessException, InvalidRequestException {
    log.info("Deleting workout with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    workoutLogRepository.delete(workoutLog);
  }

  @Override
  public WorkoutLog addExerciseLogs(String firebaseId, Long workoutLogId, List<ExerciseLog> exerciseLogs) throws DataAccessException,
      InvalidRequestException {
    log.info("Adding exercise log to workout log with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    for (var exerciseLog : exerciseLogs) {
      workoutLog = addExerciseLogToWorkoutLog(workoutLog, exerciseLog);
    }

    var currentPositions = getExerciseLogPositions(workoutLog.getExerciseLogs());
    return updateAndSimplifyExerciseLogPositions(workoutLog, currentPositions);
  }

  @Override
  public WorkoutLog updateExerciseLogPositions(String firebaseId, Long workoutLogId, Map<Long, Integer> newPositions)
      throws DataAccessException, InvalidRequestException {
    log.info("Updating the positions of exercise logs in workout log with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    return updateAndSimplifyExerciseLogPositions(workoutLog, newPositions);
  }

  @Override
  public WorkoutLog deleteExerciseLog(String firebaseId, Long workoutLogId, Long exerciseLogId) throws DataAccessException, InvalidRequestException {
    log.info("Deleting exercise log with ID {} from workout with ID {}", exerciseLogId, workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfExerciseLogNotInWorkoutLog(exerciseLog, workoutLog);

    if (!workoutLog.removeExerciseLog(exerciseLog)) {
      log.error("Failed to remove exercise log with ID {} from workout log with ID {}", exerciseLogId, workoutLogId);
      throw new DataAccessException("An error occurred while removing the requested exercise log.", ServerError.UNDEFINED_ERROR);
    }

    // after removal of exercise log, fill gaps in positions (from {a->1, b->2, c->3, d->4} to {a->1, c->3, d->4} to {a->1, c->2, d->3})
    var currentPositions = getExerciseLogPositions(workoutLog.getExerciseLogs());
    return updateAndSimplifyExerciseLogPositions(workoutLog, currentPositions);
  }

  @Override
  public WorkoutLog setExerciseLogComment(String firebaseId, Long workoutLogId, Long exerciseLogId, String comment)
      throws DataAccessException, InvalidRequestException {
    log.info("Setting the comment of exercise log with ID {} form workout with ID {}", exerciseLogId, workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfExerciseLogNotInWorkoutLog(exerciseLog, workoutLog);

    exerciseLog.setComment(comment);

    return workoutLogRepository.save(workoutLog);
  }

  @Override
  public WorkoutLog addSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, SetLog setLog)
      throws DataAccessException, InvalidRequestException {
    log.info("Adding set log to exercise log with ID {}", exerciseLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfExerciseLogNotInWorkoutLog(exerciseLog, workoutLog);

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
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfExerciseLogNotInWorkoutLog(exerciseLog, workoutLog);

    var setLogToUpdate = getSetLogOrThrow(setLogId);
    throwIfSetLogNotInExerciseLog(setLogToUpdate, exerciseLog);

    if (!Objects.equals(setLogToUpdate.getPosition(), setLog.getPosition())) {
      log.error("Position of set log must not be changed during update.");
      throw new InvalidRequestException("Position of set log may only be changed via the designated endpoint operation.",
          ServerError.SET_LOG_POSITION_CHANGE_FORBIDDEN);
    }

    validateLoggingType(exerciseLog.getExercise(), setLog);
    var indexToUpdate = exerciseLog.getSetLogs().indexOf(setLogToUpdate);
    exerciseLog.removeSetLog(indexToUpdate);
    exerciseLog.addSetLog(indexToUpdate, setLog);
    exerciseLogRepository.save(exerciseLog);

    return workoutLog;
  }

  @Override
  public WorkoutLog updateSetLogPositions(String firebaseId, Long workoutLogId, Long exerciseLogId, Map<Long, Integer> newPositions)
      throws DataAccessException, InvalidRequestException {
    log.info("Updating the positions of set logs in exercise log with ID {}", exerciseLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfExerciseLogNotInWorkoutLog(exerciseLog, workoutLog);

    return updateAndSimplifySetLogPositions(workoutLog, exerciseLog, newPositions);
  }

  @Override
  public WorkoutLog deleteSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, Long setLogId)
      throws DataAccessException, InvalidRequestException {
    log.info("Deleting set log with ID {} from exercise log with ID {}", setLogId, exerciseLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfWorkoutLogNotByUser(firebaseId, workoutLog);

    var exerciseLog = getExerciseLogOrThrow(exerciseLogId);
    throwIfExerciseLogNotInWorkoutLog(exerciseLog, workoutLog);

    var setLog = getSetLogOrThrow(setLogId);
    throwIfSetLogNotInExerciseLog(setLog, exerciseLog);

    if (!exerciseLog.removeSetLog(setLog)) {
      log.error("Failed to remove set log with ID {} from exercise log with ID {}", setLogId, exerciseLogId);
      throw new DataAccessException("An error occurred while removing the requested set log.", ServerError.UNDEFINED_ERROR);
    }

    // after removal of set log, fill gaps in positions (from {a->1, b->2, c->3, d->4} to {a->1, c->3, d->4} to {a->1, c->2, d->3})
    var currentPositions = getSetLogPositions(exerciseLog.getSetLogs());
    updateAndSimplifySetLogPositions(workoutLog, exerciseLog, currentPositions);

    exerciseLogRepository.save(exerciseLog);
    return workoutLog;
  }

  private List<WorkoutLog> getWorkoutLogsLoggedByInPeriod(ZonedDateTime startDate, ZonedDateTime endDate,
                                                          BiFunction<ZonedDateTime, ZonedDateTime, List<WorkoutLog>> workoutLogsGetter) {
    var startOfStartDay = startDate.with(LocalTime.MIN);
    var endOfEndDay = endDate.with(LocalTime.MAX);
    return workoutLogsGetter.apply(startOfStartDay, endOfEndDay);
  }

  private WorkoutLog getWorkoutLogOrThrow(Long id) throws DataNotFoundException {
    return workoutLogRepository
        .findById(id)
        .orElseThrow(() -> new DataNotFoundException("Requested workout log does not exist.", ServerError.WORKOUT_LOG_NOT_FOUND));
  }

  private ExerciseLog getExerciseLogOrThrow(Long id) throws DataNotFoundException {
    return exerciseLogRepository
        .findById(id)
        .orElseThrow(() -> new DataNotFoundException("Requested exercise log does not exist.", ServerError.EXERCISE_LOG_NOT_FOUND));
  }

  private SetLog getSetLogOrThrow(Long id) throws DataNotFoundException {
    return setLogRepository
        .findById(id)
        .orElseThrow(() -> new DataNotFoundException("Requested set log does not exist.", ServerError.SET_LOG_NOT_FOUND));
  }

  private void throwIfWorkoutLogNotByUser(String firebaseId, WorkoutLog workoutLog) throws InvalidRequestException, DataAccessException {
    var user = getUser(userService, firebaseId);

    if (!Role.ADMIN.equals(user.getRole()) && !workoutLog.getUser().equals(user)) {
      log.error("Requested workout was not logged by user with provided Firebase ID {}.", firebaseId);
      throw new InvalidRequestException("The requested workout was not logged by the provided user.", ServerError.WORKOUT_LOG_NOT_BY_USER);
    }
  }

  private void throwIfExerciseLogNotInWorkoutLog(ExerciseLog exerciseLog, WorkoutLog workoutLog)
      throws InvalidRequestException, DataAccessException {
    if (!workoutLog.getExerciseLogs().contains(exerciseLog)) {
      log.error("Requested exercise log with ID {} is not part of requested workout log with ID {}.", exerciseLog.getId(), workoutLog.getId());
      throw new InvalidRequestException("The requested exercise is not part of the requested workout.", ServerError.EXERCISE_LOG_NOT_IN_WORKOUT_LOG);
    }

    if (!exerciseLog.getWorkoutLog().equals(workoutLog)) {
      log.error("Requested exercise log with ID {} not consistent with requested workout log with ID {}.", exerciseLog.getId(), workoutLog.getId());
      throw new DataAccessException("There are some data inconsistencies.", ServerError.UNDEFINED_ERROR);
    }
  }

  private void throwIfSetLogNotInExerciseLog(SetLog setLog, ExerciseLog exerciseLog) throws InvalidRequestException, DataAccessException {
    if (!exerciseLog.getSetLogs().contains(setLog)) {
      log.error("Requested set log with ID {} is not part of requested exercise log with ID {}.", setLog.getId(), exerciseLog.getId());
      throw new InvalidRequestException("The requested set is not part of the requested exercise.", ServerError.SET_LOG_NOT_IN_EXERCISE_LOG);
    }

    if (!setLog.getExerciseLog().equals(exerciseLog)) {
      log.error("Requested set log with ID {} not consistent with requested exercise log with ID {}.", setLog.getId(), exerciseLog.getId());
      throw new DataAccessException("There are some data inconsistencies.", ServerError.UNDEFINED_ERROR);
    }
  }

  private WorkoutLog addExerciseLogToWorkoutLog(WorkoutLog workoutLog, ExerciseLog newExerciseLog)
      throws DataNotFoundException, InvalidRequestException {
    var exercise = getExercise(exerciseService, newExerciseLog.getExercise().getId());
    var exerciseLogToPersist = newExerciseLog
        .toBuilder()
        .exercise(exercise)
        .position(workoutLog.getExerciseLogs().size() + 1)
        .setLogs(new ArrayList<>()) // persist with empty list, add SetLogs only later on to establish bidirectional relationships
        .build();

    workoutLog.addExerciseLog(exerciseLogToPersist);
    var workoutLogWithNewExerciseLog = workoutLogRepository.save(workoutLog);

    var persistedExerciseLog = Iterables.getLast(workoutLogWithNewExerciseLog.getExerciseLogs());
    for (var setLog : newExerciseLog.getSetLogs()) {
      setLog.setPosition(persistedExerciseLog.getSetLogs().size() + 1);
      addSetLogToExerciseLog(persistedExerciseLog, setLog);
    }

    var currentPositions = getSetLogPositions(persistedExerciseLog.getSetLogs());
    return updateAndSimplifySetLogPositions(workoutLogWithNewExerciseLog, persistedExerciseLog, currentPositions);
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
      throw new InvalidRequestException("Requested logging type is not valid for the specified exercise.", ServerError.INVALID_LOGGING_TYPE);
    }
  }

  private void updateLogPositions(Supplier<Map<Long, Integer>> currentPositionSupplier, Map<Long, Integer> newPositions,
                                  String invalidSpecificationErrorMessage, String duplicateSpecificationErrorMessage,
                                  Consumer<Map<Long, Integer>> newPositionsApplicator) throws InvalidRequestException {
    var currentPositions = currentPositionSupplier.get();

    if (!newPositions.keySet().equals(currentPositions.keySet())) {
      log.error(invalidSpecificationErrorMessage);
      throw new InvalidRequestException(invalidSpecificationErrorMessage, ServerError.POSITION_MAP_INVALID);
    }

    if (!newPositions.values().stream().allMatch(new HashSet<>()::add)) {
      log.error(duplicateSpecificationErrorMessage);
      throw new InvalidRequestException(duplicateSpecificationErrorMessage, ServerError.POSITION_MAP_NOT_UNIQUE);
    }

    var newPositionsWithoutGaps = simplifyPositionSpecification(newPositions);
    newPositionsApplicator.accept(newPositionsWithoutGaps);
  }

  private WorkoutLog updateAndSimplifyExerciseLogPositions(WorkoutLog workoutLog, Map<Long, Integer> newPositions) throws InvalidRequestException {
    updateLogPositions(() -> getExerciseLogPositions(workoutLog.getExerciseLogs()),
        newPositions,
        "The map of new exercise log positions must exactly cover the exercise logs of the given workout log.",
        "The assignment of exercise logs to new positions must be unique.",
        positions -> workoutLog.getExerciseLogs().forEach(log1 -> log1.setPosition(positions.get(log1.getId()))));

    return workoutLogRepository.save(workoutLog);
  }

  private WorkoutLog updateAndSimplifySetLogPositions(WorkoutLog workoutLog, ExerciseLog exerciseLog, Map<Long, Integer> newPositions)
      throws InvalidRequestException {
    updateLogPositions(() -> getSetLogPositions(exerciseLog.getSetLogs()),
        newPositions,
        "The map of new set log positions must exactly cover the set logs of the given exercise log.",
        "The assignment of set logs to new positions must be unique.",
        positionsWithoutGaps -> exerciseLog.getSetLogs().forEach(setLog -> setLog.setPosition(positionsWithoutGaps.get(setLog.getId()))));

    return workoutLogRepository.save(workoutLog);
  }

  private Map<Long, Integer> getExerciseLogPositions(List<ExerciseLog> exerciseLogs) {
    return exerciseLogs.stream()
        .collect(Collectors.toMap(ExerciseLog::getId, ExerciseReference::getPosition));
  }

  private Map<Long, Integer> getSetLogPositions(List<SetLog> setLogs) {
    return setLogs.stream()
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
