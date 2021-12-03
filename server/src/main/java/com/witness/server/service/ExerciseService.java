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
   * @return an {@link Exercise} instance whose {@link Exercise#getId()} property is equal to the provided {@code exerciseId}
   * @throws DataNotFoundException if there is no {@link Exercise} with the given {@code exerciseId}
   */
  Exercise getExerciseById(Long exerciseId) throws DataNotFoundException;

  /**
   * Fetches the user exercise with the provided {@code exerciseId}.
   *
   * @param exerciseId ID of the exercise to fetch
   * @return an {@link UserExercise} instance whose {@link UserExercise#getId()} property is equal to the provided {@code exerciseId}
   * @throws DataNotFoundException if there is no {@link UserExercise} with the given {@code exerciseId}
   */
  UserExercise getUserExerciseById(Long exerciseId) throws DataNotFoundException;
}
