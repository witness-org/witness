package com.witness.server.service;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.workout.ExerciseLog;
import com.witness.server.entity.workout.SetLog;
import com.witness.server.entity.workout.WorkoutLog;
import com.witness.server.enumeration.LoggingType;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

/**
 * Provides methods related to managing workout logs.
 */
public interface WorkoutLogService {

  /**
   * Fetches workout logs that were logged on a specific day by the user with the provided Firebase ID.
   *
   * @param firebaseId the Firebase ID of the user whose workout logs from {@code date} should be fetched
   * @param date       the day for which logged workouts should be fetched. The time part is only relevant for timezone offsets.
   * @return a list of {@link WorkoutLog} instances that were logged on the day represented by {@code date} and by the user with Firebase ID
   *     {@code firebaseId}.
   */
  List<WorkoutLog> getWorkoutLogsOfDay(String firebaseId, ZonedDateTime date);

  /**
   * Creates a new workout log in the name of the user with the provided Firebase ID.
   *
   * @param workoutLog the workout log to be persisted
   * @param firebaseId the Firebase ID of the user for whom {@code workoutLogId} should be logged
   * @return the {@link WorkoutLog} that is now saved in the database
   * @throws DataNotFoundException   if the database does not contain a user with the given Firebase ID or if the exercise referenced by
   *                                 {@code workoutLogId} does not exist
   * @throws DataAccessException     if an error occurs during user lookup
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     a {@link SetLog} with a {@link LoggingType} that is not valid for the referenced {@link Exercise} is used
   *                                   </li>
   *                                   <li>
   *                                     an error occurs during validation and/or rectification of {@code position} properties of exercise or set logs
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog createWorkoutLog(WorkoutLog workoutLog, String firebaseId) throws DataAccessException, InvalidRequestException;

  /**
   * Sets the duration of an existing workout log.
   *
   * @param firebaseId   the Firebase ID of the user that is executing this operation. Must be admin or the user who has created the affected workout
   *                     log.
   * @param workoutLogId the ID of the workout log whose duration should be set
   * @param duration     the duration in minutes that should be set for the workout log represented by {@code workoutLogId}
   * @return the {@link WorkoutLog} whose {@link WorkoutLog#getDurationMinutes()} property has been set to {@code duration} minutes
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId} or the user represented by {@code firebaseId} does not
   *                                 exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                 identified by {@code workoutLogId}
   */
  WorkoutLog setWorkoutDuration(String firebaseId, Long workoutLogId, Integer duration) throws DataAccessException, InvalidRequestException;

  /**
   * Deletes the workout log with the provided ID.
   *
   * @param firebaseId   the Firebase ID of the user executing the operation
   * @param workoutLogId the ID of the {@link WorkoutLog} to be deleted
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId} or the user represented by {@code firebaseId} does not
   *                                 exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                 identified by {@code workoutLogId}
   */
  void deleteWorkoutLog(String firebaseId, Long workoutLogId) throws DataAccessException, InvalidRequestException;

  /**
   * Adds an exercise log to an existing workout log. The {@link ExerciseLog#getPosition()} property is set to be one higher than the current highest
   * position.
   *
   * @param firebaseId   the Firebase ID of the user executing the operation
   * @param workoutLogId the ID of the {@link WorkoutLog} the new exercise log should be added to
   * @param exerciseLog  the {@link ExerciseLog} to be added to the workout log represented by {@code workoutLogId}
   * @return the modified {@link WorkoutLog}
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId}, the exercise referenced by {@code exerciseLog} or the user
   *                                 represented by {@code firebaseId} does not exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     a {@link SetLog} with a {@link LoggingType} that is not valid for the referenced {@link Exercise} is used
   *                                   </li>
   *                                   <li>
   *                                     an error occurs during validation and/or rectification of {@code position} properties of set logs
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog addExerciseLog(String firebaseId, Long workoutLogId, ExerciseLog exerciseLog) throws DataAccessException, InvalidRequestException;

  /**
   * Updates the {@link ExerciseLog#getPosition()} properties of the exercise logs of a workout log.
   *
   * @param firebaseId   the Firebase ID of the user executing the operation
   * @param workoutLogId the ID of the {@link WorkoutLog} whose exercise log positions should be updated
   * @param newPositions a map that associates the ID of {@link ExerciseLog} items of the {@link WorkoutLog#getExerciseLogs()} property of the
   *                     {@link WorkoutLog} instance represented by {@code workoutLogId} a new position. The mapping must be unique (i.e. no two IDs
   *                     are associated with the same position) and for every ID, there must be an associations Furthermore, positions must be
   *                     positive. The mappings need not be gapless, i.e. their value set need not form an integer sequence - gaps are removed
   *                     internally.
   * @return the modified {@link WorkoutLog} with updated exercise log positions according to {@code newPositions}
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId} or the user represented by {@code firebaseId} does not
   *                                 exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the new position specification, represented by {@code newPositions}, does not exactly cover all IDs of the
   *                                     {@link WorkoutLog#getExerciseLogs()} property of the {@link WorkoutLog} represented by {@code workoutLogId}
   *                                     (i.e. at least one ID is missing and/or an ID that is not part of the workout log's exercise logs is present)
   *                                   </li>
   *                                   <li>
   *                                     the new position specification, represented by {@code newPositions}, contains duplicate values (i.e. there
   *                                     are two IDs which map to the same position)
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog updateExerciseLogPositions(String firebaseId, Long workoutLogId, Map<Long, Integer> newPositions)
      throws DataAccessException, InvalidRequestException;

  /**
   * Deletes an exercise log from a workout log and rearranges {@link ExerciseLog#getPosition()} properties accordingly, i.e. the resulting gap is
   * closed by decreasing subsequent position values.
   *
   * @param firebaseId    the Firebase ID of the user executing the operation
   * @param workoutLogId  the ID of the {@link WorkoutLog} from which the {@link ExerciseLog} should be deleted
   * @param exerciseLogId the ID of the {@link ExerciseLog} to delete
   * @return the modified {@link WorkoutLog} without the {@link ExerciseLog} represented by {@code exerciseLogId}
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId}, the user represented by {@code firebaseId} or the exercise
   *                                 log represented by {@code exerciseLogId} does not exist
   * @throws DataAccessException     if the user lookup fails or the exercise log represented by {@code exerciseLogId} could not be removed from the
   *                                 workout log represented by {@code workoutLogId}
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     an error occurs during validation and/or rectification of {@code position} properties of exercise or set logs
   *                                   </li>
   *                                   <li>
   *                                     the exercise log represented by {@code exerciseLogId} is not part of the workout log represented by
   *                                     {@code workoutLogId}
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog deleteExerciseLog(String firebaseId, Long workoutLogId, Long exerciseLogId) throws DataAccessException, InvalidRequestException;

  /**
   * Sets the {@code comment} property of an existing exercise log.
   *
   * @param firebaseId    the Firebase ID of the user executing the operation
   * @param workoutLogId  the ID of the {@link WorkoutLog} containing the {@link ExerciseLog} whose {@link ExerciseLog#getComment()} property should
   *                      be set
   * @param exerciseLogId the ID of the {@link ExerciseLog} whose {@link ExerciseLog#getComment()} property should be set
   * @param comment       the comment to set for the {@link ExerciseLog} represented by {@code comment}
   * @return the {@link WorkoutLog} containing the modified {@link ExerciseLog} represented by {@code exerciseLogId} with the
   *     {@link ExerciseLog#getComment()} equal to {@code comment}
   * @throws DataNotFoundException   if the workout represented by {@code workoutLogId}, the user represented by {@code firebaseId} or the exercise
   *                                 log represented by {@code exerciseLogId} does not exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the exercise log represented by {@code exerciseLogId} is not part of the workout log represented by
   *                                     {@code workoutLogId}
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog setExerciseLogComment(String firebaseId, Long workoutLogId, Long exerciseLogId, String comment)
      throws DataAccessException, InvalidRequestException;

  /**
   * Adds a set log to an existing set log. The {@link SetLog#getPosition()} property is set to be one higher than the current highest position.
   *
   * @param firebaseId    the Firebase ID of the user executing the operation
   * @param workoutLogId  the ID of the {@link WorkoutLog} the new set log should be added to
   * @param exerciseLogId the ID of the {@link ExerciseLog} that belongs to the {@link WorkoutLog} represented by {@code workoutLogId} the new set log
   *                      should be added to
   * @param setLog        the new {@link SetLog} to add to the {@link ExerciseLog} represented by {@code exerciseLogId}
   * @return the modified {@link WorkoutLog} with {@code setLog} having been added to the {@link ExerciseLog} represented by {@code exerciseLogId}
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId}, the exercise log represented by {@code exerciseLog} or the
   *                                 user represented by {@code firebaseId} does not exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the exercise log represented by {@code exerciseLogId} is not part of the workout log represented by
   *                                     {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     a {@link SetLog} with a {@link LoggingType} that is not valid for the referenced {@link Exercise} is used
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog addSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, SetLog setLog) throws DataAccessException, InvalidRequestException;

  /**
   * Updates an existing set log. The {@link SetLog#getPosition()} property must not change.
   *
   * @param firebaseId    the Firebase ID of the user executing the operation
   * @param workoutLogId  the ID of the {@link WorkoutLog} the set log to update belongs to
   * @param exerciseLogId the ID of the {@link ExerciseLog} that belongs to the {@link WorkoutLog} represented by {@code workoutLogId} the set log
   *                      to be updated belongs to
   * @param setLog        the updated {@link SetLog}
   * @return the modified {@link WorkoutLog} with updated {@link SetLog} as specified by {@code setlog}
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId}, the user represented by {@code firebaseId}, the exercise
   *                                 log represented by {@code exerciseLogId} or the set log identified by the ID of {@code setLog} does not exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the exercise log represented by {@code exerciseLogId} is not part of the workout log represented by
   *                                     {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the set log represented by {@code setLog} is not part of the exercise log represented by
   *                                     {@code exerciseLogId}
   *                                   </li>
   *                                   <li>
   *                                     it was tried to change the {@link SetLog#getPosition()} property of the set log to update
   *                                   </li>
   *                                   <li>
   *                                     the set log to update references an exercise for which the {@link SetLog}'s associated {@link LoggingType}
   *                                     is not valid according to the corresponding exercise's {@link Exercise#getLoggingTypes()} property
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog updateSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, SetLog setLog)
      throws DataAccessException, InvalidRequestException;

  /**
   * Updates the {@link SetLog#getPosition()} properties of the set logs of an exercise log within a workout log.
   *
   * @param firebaseId    the Firebase ID of the user executing the operation
   * @param workoutLogId  the ID of the {@link WorkoutLog} containing the exercise log whose set log positions should be updated
   * @param exerciseLogId the ID of the {@link ExerciseLog} whose set log positions should be updated
   * @param newPositions  a map that associates the ID of {@link SetLog} items of the {@link ExerciseLog#getSetLogs()} property of the
   *                      {@link ExerciseLog} instance represented by {@code exerciseLogId} a new position. The mapping must be unique (i.e. no two
   *                      IDs are associated with the same position) and for every ID, there must be an association. Furthermore, positions must be
   *                      positive. The mappings need not be gapless, i.e. their value set need not form an integer sequence - gaps are removed
   *                      internally.
   * @return the modified {@link WorkoutLog} with updated set log positions according to {@code newPositions}
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId}, the user represented by {@code firebaseId} or the exercise
   *                                 log represented by {@code exerciseLogId} does not exist
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the exercise log represented by {@code exerciseLogId} is not part of the workout log represented by
   *                                     {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the new position specification, represented by {@code newPositions}, does not exactly cover all IDs of the
   *                                     {@link WorkoutLog#getExerciseLogs()} property of the {@link WorkoutLog} represented by {@code workoutLogId}
   *                                     (i.e. at least one ID is missing and/or an ID that is not part of the workout log's exercise logs is present)
   *                                   </li>
   *                                   <li>
   *                                     the new position specification, represented by {@code newPositions}, contains duplicate values (i.e. there
   *                                     are two IDs which map to the same position)
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog updateSetLogPositions(String firebaseId, Long workoutLogId, Long exerciseLogId, Map<Long, Integer> newPositions)
      throws DataAccessException, InvalidRequestException;

  /**
   * Deletes a set log from an exercise log and rearranges {@link SetLog#getPosition()} properties accordingly, i.e. the resulting gap is
   * closed by decreasing subsequent position values.
   *
   * @param firebaseId    the Firebase ID of the user executing the operation
   * @param workoutLogId  the ID of the {@link WorkoutLog} containing the {@link ExerciseLog} from which the {@link SetLog} should be deleted
   * @param exerciseLogId the ID of the {@link ExerciseLog} containing the {@link SetLog} to be removed
   * @param setLogId      the ID of the {@link SetLog} to be removed
   * @return the modified {@link WorkoutLog} without the {@link SetLog} represented by {@code setLogId}
   * @throws DataNotFoundException   if the workout log identified by {@code workoutLogId}, the user represented by {@code firebaseId}, the exercise
   *                                 log represented by {@code exerciseLogId} or the set log represented by {@code setLogId} does not exist
   * @throws DataAccessException     if the user lookup fails or the set log represented by {@code setLogId} could not be removed from the
   *                                 exercise log represented by {@code exerciseLogId}
   * @throws InvalidRequestException if one of the following is true:
   *                                 <ul>
   *                                   <li>
   *                                     the user represented by {@code firebaseId} is neither admin nor the one who initially created the workout log
   *                                     identified by {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     an error occurs during validation and/or rectification of {@code position} properties of exercise or set logs
   *                                   </li>
   *                                   <li>
   *                                     the exercise log represented by {@code exerciseLogId} is not part of the workout log represented by
   *                                     {@code workoutLogId}
   *                                   </li>
   *                                   <li>
   *                                     the set log represented by {@code setLogId} is not part of the exercise log represented by
   *                                     {@code exerciseLogId}
   *                                   </li>
   *                                 </ul>
   */
  WorkoutLog deleteSetLog(String firebaseId, Long workoutLogId, Long exerciseLogId, Long setLogId) throws DataAccessException,
      InvalidRequestException;
}
