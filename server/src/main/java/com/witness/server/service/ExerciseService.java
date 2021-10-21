package com.witness.server.service;

import com.witness.server.entity.Exercise;
import com.witness.server.entity.UserExercise;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.InvalidRequestException;
import java.util.List;

/**
 * Provides methods related to managing Exercises or UserExercises.
 */
public interface ExerciseService {

  /**
   * Creates a new initial exercise, i.e. an exercise that is available to every user.
   *
   * @param exercise exercise to be created
   * @return persisted Exercise object
   * @throws InvalidRequestException if there is already an initial exercise with the name of the requested {@code exercise}
   */
  Exercise createInitialExercise(Exercise exercise) throws InvalidRequestException;

  /**
   * Creates a new user exercise, i.e. an exercise that will only be available to the logged-in user.
   *
   * @param exercise user exercise to be created
   * @return persisted UserExercise object
   * @throws DataAccessException     if the logged-in user is not found in the database
   * @throws InvalidRequestException if there is already an initial exercise or user exercise created by the logged-in user with the name of the
   *                                 requested {@code exercise}
   */
  UserExercise createUserExercise(UserExercise exercise) throws DataAccessException, InvalidRequestException;

  /**
   * Fetches all exercises contained in the "repertoire" of the logged-in user that train a given muscle group. The "repertoire" consists of the
   * initial exercises (i.e. Exercise objects, available to every user) and the user's user exercises (i.e. UserExercise objects, only available to
   * the user that created them).
   *
   * @param muscleGroup that should be trained with the fetched exercises
   * @return list of exercises in the "repertoire" of the logged-in user for the {@code muscleGroup}
   * @throws DataAccessException if the logged-in user is not found in the database
   */
  List<Exercise> getExercisesForUserByMuscleGroup(MuscleGroup muscleGroup) throws DataAccessException;

  /**
   * Fetches all exercises that were created by the logged-in user.
   *
   * @return list of exercises that were created by the logged-in user
   * @throws DataAccessException if the logged-in user is not found in the database
   */
  List<Exercise> getExercisesCreatedByUser() throws DataAccessException;

}
