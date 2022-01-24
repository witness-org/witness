package com.witness.server.service;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.exercise.UserExercise;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.exception.InvalidRequestException;
import java.util.List;

/**
 * Provides methods related to managing exercises (both initial exercises and user exercises).
 */
public interface ExerciseService {

  /**
   * Creates a new initial exercise, i.e. an exercise that is available to every user.
   *
   * @param exercise exercise to be created
   * @return persisted {@link Exercise} object
   * @throws InvalidRequestException if there is already an initial exercise with the name of the requested {@code exercise}
   */
  Exercise createInitialExercise(Exercise exercise) throws InvalidRequestException;

  /**
   * Creates a new user exercise, i.e. an exercise that will only be available to the user with the provided Firebase ID.
   *
   * @param firebaseId Firebase ID of the user that creates the user exercise
   * @param exercise   user exercise to be created
   * @return persisted {@link UserExercise} object
   * @throws DataAccessException     if the user with the provided {@code firebaseId} is not found in the database
   * @throws InvalidRequestException if there is already an initial exercise or user exercise created by the user with the provided {@code firebaseId}
   *                                 with the name of the requested {@code exercise}
   */
  UserExercise createUserExercise(String firebaseId, UserExercise exercise) throws DataAccessException, InvalidRequestException;

  /**
   * Updates an initial exercise, i.e. an exercise that is available to every user.
   *
   * @param exercise updated exercise
   * @return updated {@link Exercise} object
   * @throws InvalidRequestException if there is already an initial exercise with the updated name of the requested {@code exercise}
   * @throws DataNotFoundException   if the exercise to update does not exist
   */
  Exercise updateInitialExercise(Exercise exercise) throws InvalidRequestException, DataNotFoundException;

  /**
   * Updates a user exercise, i.e. an exercise that will only be available to the user with the provided Firebase ID.
   *
   * @param firebaseId Firebase ID of the user that updates the user exercise
   * @param exercise   updated user exercise
   * @return updated {@link UserExercise} object
   * @throws DataAccessException     if the user with the provided {@code firebaseId} is not found in the database
   * @throws InvalidRequestException if there is already an initial exercise or user exercise created by the user with the provided {@code firebaseId}
   *                                 with the updated name of the requested {@code exercise} or if the user with the provided {@code firebaseId} is
   *                                 not an admin and the requested {@code exercise} was not created by them
   */
  UserExercise updateUserExercise(String firebaseId, Exercise exercise) throws DataAccessException, InvalidRequestException;

  /**
   * Fetches all exercises contained in the "repertoire" of the user with the given Firebase ID that train the given muscle group. The "repertoire"
   * consists of the initial exercises (i.e. {@link Exercise} objects, available to every user) and the user's user exercises (i.e.
   * {@link UserExercise} objects, only available to the user that created them).
   *
   * @param firebaseId  Firebase ID of the user for whom the exercises should be fetched
   * @param muscleGroup that should be trained with the fetched exercises
   * @return list of exercises in the "repertoire" of the user with the provided {@code firebaseId} for the {@code muscleGroup}
   * @throws DataAccessException if the user with the provided {@code firebaseId} is not found in the database
   */
  List<Exercise> getExercisesForUserByMuscleGroup(String firebaseId, MuscleGroup muscleGroup) throws DataAccessException;

  /**
   * Fetches all exercises that were created by the user with the given Firebase ID.
   *
   * @param firebaseId Firebase ID of the user whose exercises should be fetched
   * @return list of exercises that were created by the user with the provided {@code firebaseId}
   * @throws DataAccessException if the user with the provided {@code firebaseId} is not found in the database
   */
  List<Exercise> getExercisesCreatedByUser(String firebaseId) throws DataAccessException;

  /**
   * Fetches the exercise with the provided {@code exerciseId}.
   *
   * @param exerciseId ID of the exercise to fetch
   * @return an {@link Exercise} instance whose {@link Exercise#getId()} property is equal to the provided {@code exerciseId}. Might also be a
   *     {@link UserExercise} instance. In order to search initial exercises only, use {@link ExerciseService#getInitialExerciseById(Long)}. In order
   *     to search user exercises only, use {@link ExerciseService#getUserExerciseById(Long)}.
   * @throws DataNotFoundException if there is no {@link Exercise} with the given {@code exerciseId}
   */
  Exercise getExerciseById(Long exerciseId) throws DataNotFoundException;

  /**
   * Fetches the initial exercise with the provided {@code exerciseId}.
   *
   * @param initialExerciseId ID of the initial exercise to fetch
   * @return an {@link Exercise} instance whose {@link Exercise#getId()} property is equal to the provided {@code exerciseId}. Is never an instance
   *     of {@link UserExercise}. In order to search both initial and user exercises, use {@link ExerciseService#getExerciseById(Long)}
   * @throws DataNotFoundException if there is no initial {@link Exercise} with the given {@code exerciseId}
   */
  Exercise getInitialExerciseById(Long initialExerciseId) throws DataNotFoundException;

  /**
   * Fetches the user exercise with the provided {@code exerciseId}.
   *
   * @param userExerciseId ID of the user exercise to fetch
   * @return a {@link UserExercise} instance whose {@link UserExercise#getId()} property is equal to the provided {@code userExerciseId}. In order to
   *     search initial exercises only, use {@link ExerciseService#getInitialExerciseById(Long)}. In order to search both initial and user exercises,
   *     use {@link ExerciseService#getExerciseById(Long)}.
   * @throws DataNotFoundException if there is no {@link UserExercise} with the given {@code userExerciseId}
   */
  UserExercise getUserExerciseById(Long userExerciseId) throws DataNotFoundException;

  /**
   * Deletes the initial exercise with the given id.
   *
   * @param initialExerciseId the ID of the {@link Exercise} to be deleted
   * @throws DataNotFoundException if there is no initial {@link Exercise} with the given {@code exerciseId}
   */
  void deleteInitialExercise(Long initialExerciseId) throws DataNotFoundException;

  /**
   * Deletes the user exercise with the given id.
   *
   * @param firebaseId     the Firebase ID of the user executing the operation
   * @param userExerciseId the ID of the {@link UserExercise} to be deleted
   * @throws DataNotFoundException   if there is no {@link UserExercise} with the given {@code userExerciseId}
   * @throws DataAccessException     if the user lookup fails
   * @throws InvalidRequestException if the user represented by {@code firebaseId} is neither admin nor the one who created the user exercise
   *                                 identified by {@code userExerciseId}
   */
  void deleteUserExercise(String firebaseId, Long userExerciseId) throws DataNotFoundException, DataAccessException, InvalidRequestException;
}
