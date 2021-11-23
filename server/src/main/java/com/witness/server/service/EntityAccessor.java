package com.witness.server.service;

import com.witness.server.entity.exercise.Exercise;
import com.witness.server.entity.user.User;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;

/**
 * Often times, services need to access other entities in order to fully populate or validate objects to persist. This interface provides methods
 * that solve these cross-cutting concerns. Since Java does not have in-built support for mixins, the methods are equipped with default
 * implementations that rely on consumer-controlled service implementations. In case of more specific requirements, implementing services may
 * override affected methods.
 */
public interface EntityAccessor {

  /**
   * Retrieves the {@link User} with the provided {@code firebaseId} utilizing the provided {@code userService}.
   *
   * @param userService {@link UserService} implementation to be used to find the user
   * @param firebaseId  Firebase ID of the user
   * @return a {@link User} object whose {@link User#getFirebaseId()} property is equal to the provided {@code firebaseId}
   * @throws DataNotFoundException if the database does not contain a user with the given Firebase ID
   * @throws DataAccessException   if an error occurs during user lookup
   */
  default User getUser(UserService userService, String firebaseId) throws DataAccessException {
    return userService.findByFirebaseId(firebaseId);
  }

  /**
   * Retrieves the {@link Exercise} with the provided {@code exerciseId} utilizing the provided {@code exerciseService}.
   *
   * @param exerciseService {@link ExerciseService} implementation to be used to find the exercise
   * @param exerciseId      ID of the exercise
   * @return a {@link Exercise} instance whose {@link Exercise#getId()} property is equal to the provided {@code exerciseId}
   * @throws DataNotFoundException if there is no {@link Exercise} with the given {@code exerciseId}
   */
  default Exercise getExercise(ExerciseService exerciseService, Long exerciseId) throws DataNotFoundException {
    return exerciseService.getExerciseById(exerciseId);
  }
}
