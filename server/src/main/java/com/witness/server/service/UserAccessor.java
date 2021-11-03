package com.witness.server.service;

import com.witness.server.entity.user.User;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataNotFoundException;

/**
 * Provides methods for accessing users via {@link UserService} implementations and corresponding default implementations that may be overridden.
 */
public interface UserAccessor {

  /**
   * Retrieves the user with the provided {@code firebaseId} utilizing the provided {@code userService}.
   *
   * @param userService user service that is used to find the user
   * @param firebaseId Firebase ID of the user
   * @return user with the provided {@code firebaseId}
   * @throws DataNotFoundException if the database does not contain a user with the given Firebase ID
   * @throws DataAccessException if an error occurs during user lookup
   */
  default User getUser(UserService userService, String firebaseId) throws DataAccessException {
    return userService.findByFirebaseId(firebaseId);
  }
}
