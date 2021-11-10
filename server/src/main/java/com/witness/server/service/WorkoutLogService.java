package com.witness.server.service;

import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.InvalidRequestException;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Provides methods related to logging a workout.
 */
public interface WorkoutLogService {

  List<WorkoutLog> getWorkoutLogs(String firebaseId, ZonedDateTime date);

  WorkoutLog createWorkoutLog(WorkoutLog workoutLog, String firebaseId) throws DataAccessException, InvalidRequestException;

  WorkoutLog setWorkoutDuration(String firebaseId, Long workoutLogId, Integer duration) throws DataAccessException, InvalidRequestException;

  void deleteWorkoutLog(String firebaseId, Long workoutLogId) throws DataAccessException, InvalidRequestException;

  WorkoutLog addExerciseLog(String firebaseId, Long workoutLogId, ExerciseLog exerciseLog) throws DataAccessException, InvalidRequestException;

  WorkoutLog deleteExerciseLog(String firebaseId, Long workoutLogId, Long exerciseLogId) throws DataAccessException, InvalidRequestException;

  WorkoutLog setExerciseLogComment(String firebaseId, Long workoutLogId, Long exerciseLogId, String comment)
      throws DataAccessException, InvalidRequestException;

  WorkoutLog addSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, SetLog setLog) throws DataAccessException, InvalidRequestException;

  WorkoutLog updateSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, SetLog setLog)
      throws DataAccessException, InvalidRequestException;

  WorkoutLog deleteSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, Long setLogId) throws DataAccessException,
      InvalidRequestException;
}
