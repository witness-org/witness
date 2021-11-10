package com.witness.server.service.impl;

import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import com.witness.server.mapper.ExerciseLogMapper;
import com.witness.server.mapper.WorkoutLogMapper;
import com.witness.server.repository.ExerciseLogRepository;
import com.witness.server.repository.SetLogRepository;
import com.witness.server.repository.WorkoutLogRepository;
import com.witness.server.service.EntityAccessor;
import com.witness.server.service.ExerciseService;
import com.witness.server.service.TimeService;
import com.witness.server.service.UserService;
import com.witness.server.service.WorkoutLogService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkoutLogServiceImpl implements WorkoutLogService, EntityAccessor {

  // TODO properly handle ordering of exercise/set logs (upon DELETE, fix ordering etc.)

  private final ExerciseService exerciseService;
  private final WorkoutLogRepository workoutLogRepository;
  private final ExerciseLogRepository exerciseLogRepository;
  private final SetLogRepository setLogRepository;
  private final UserService userService;
  private final TimeService timeService;
  private final WorkoutLogMapper workoutLogMapper;
  private final ExerciseLogMapper exerciseLogMapper;

  @Autowired
  public WorkoutLogServiceImpl(ExerciseService exerciseService, WorkoutLogRepository workoutLogRepository,
                               ExerciseLogRepository exerciseLogRepository, SetLogRepository setLogRepository, UserService userService,
                               TimeService timeService, WorkoutLogMapper workoutLogMapper, ExerciseLogMapper exerciseLogMapper) {
    this.exerciseService = exerciseService;
    this.workoutLogRepository = workoutLogRepository;
    this.exerciseLogRepository = exerciseLogRepository;
    this.setLogRepository = setLogRepository;
    this.userService = userService;
    this.timeService = timeService;
    this.workoutLogMapper = workoutLogMapper;
    this.exerciseLogMapper = exerciseLogMapper;
  }

  @Override
  public WorkoutLog createWorkoutLog(WorkoutLog workoutLog, String firebaseId) throws DataAccessException, InvalidRequestException {
    log.info("Creating new workout for user with Firebase ID {}", firebaseId);

    var user = getUser(userService, firebaseId);

    final var exerciseLogs = List.copyOf(workoutLog.getExerciseLogs());
    workoutLog.getExerciseLogs().clear();
    workoutLog.setUser(user);
    workoutLog.setLoggedOn(timeService.getCurrentTime());

    var persistedWorkoutLog = workoutLogRepository.save(workoutLog);

    for (ExerciseLog exerciseLog : exerciseLogs) {
      var exercise = getExercise(exerciseService, exerciseLog.getExercise().getId());
      exerciseLog.setExercise(exercise);

      final var setLogs = List.copyOf(exerciseLog.getSetLogs());
      exerciseLog.getSetLogs().clear();

      persistedWorkoutLog.addExerciseLog(exerciseLog);
      persistedWorkoutLog = workoutLogRepository.save(persistedWorkoutLog);

      for (SetLog setLog : setLogs) {
        var workoutExerciseLogs = persistedWorkoutLog.getExerciseLogs();
        var persistedExerciseLog = workoutExerciseLogs.get(workoutExerciseLogs.size() - 1);
        addSetLogToExerciseLog(persistedExerciseLog, setLog);
        exerciseLogRepository.save(persistedExerciseLog);
      }
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
  public WorkoutLog addExerciseLog(String firebaseId, Long workoutLogId, Long exerciseId) throws DataAccessException,
      InvalidRequestException {
    log.info("Adding exercise log to workout log with ID {}", workoutLogId);

    var workoutLog = getWorkoutLogOrThrow(workoutLogId);
    throwIfLoggedWorkoutNotByUser(firebaseId, workoutLog);

    var exercise = getExercise(exerciseService, exerciseId);
    var exerciseLog = exerciseLogMapper.fromExercise(exercise);

    // TODO validate logging type

    exerciseLog.setPosition(workoutLog.getExerciseLogs().size() + 1);

    workoutLog.addExerciseLog(exerciseLog);
    return workoutLogRepository.save(workoutLog);
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

    // TODO validate logging type

    setLog.setPosition(exerciseLog.getSetLogs().size() + 1);

    exerciseLog.addSetLog(setLog);
    exerciseLogRepository.save(exerciseLog);

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

    // TODO validate position (position must not change?)
    // TODO validate logging type

    setLog.setExerciseLog(exerciseLog);
    setLogRepository.save(setLog);

    return workoutLog;
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

  private void addSetLogToExerciseLog(ExerciseLog exerciseLog, SetLog setLog) throws InvalidRequestException {
    var loggingTypes = exerciseLog.getExercise().getLoggingTypes();
    var setLogType = LoggingType.fromLog(setLog.getClass());
    if (!loggingTypes.contains(setLogType)) {
      log.error("Logging type {} is not valid for exercise with ID {}.", setLog.getClass().getSimpleName(), exerciseLog.getExercise().getId());
      throw new InvalidRequestException("Requested log is not valid for the specified exercise");
    }

    exerciseLog.addSetLog(setLog);
  }
}
