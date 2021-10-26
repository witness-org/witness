package com.witness.server.service;

import com.witness.server.entity.User;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;

/**
 * Provides methods related to user management. This service goes in tandem with {@link FirebaseService}, i.e. some methods may invoke
 * Firebase-related operations.
 *
 * @see FirebaseService
 */
public interface UserService {
  /**
   * Creates a new user entry at the Firebase authentication server and in the application-managed database. If {@code user} has a role set, it is
   * also set for the user by means of a custom claim and a separate Firebase request.
   *
   * @param user     The user to create in the database. Values of fields {@link User#getId()}, {@link User#getCreatedAt()},
   *                 {@link User#getModifiedAt()} and {@link User#getFirebaseId()} are ignored since they are generated by the service, database or
   *                 Firebase.
   * @param password The password which should be used to authenticate the new user. It is not stored in this application, but only transmitted to
   *                 Firebase for it to create the new user record.
   * @return The created user object. The generated field values are as follows: {@link User#getId()} is a unique ID generated by the database,
   *     {@link User#getCreatedAt()} and {@link User#getModifiedAt()} are timestamps equivalent to the time of invocation of this method and
   *     {@link User#getFirebaseId()} contains the ID the Firebase server generated for the newly created user record.
   * @throws DataCreationException     if creating the Firebase record fails. See {@link DataCreationException#getCause()} for more details.
   * @throws DataNotFoundException     should only happen if concurrency is an issue, see documentation of
   *                                   {@link FirebaseService#setRole(String, Role)}
   * @throws DataModificationException see documentation of {@link FirebaseService#setRole(String, Role)}
   */
  User createUser(User user, String password) throws DataCreationException, DataNotFoundException, DataModificationException;

  /**
   * Sets a user's role to exactly one role, in the database and on the Firebase server. Clients need to re-authenticate (login) since
   * this operation revokes all refresh tokens associated with the corresponding user.
   *
   * @param userId the id of the user whose role should be set
   * @param role   the role userId should receive
   * @return the modified user
   * @throws DataNotFoundException if the database does not contain a user with primary key {@code userId}
   * @throws DataAccessException   if an error occurs during user lookup, e.g. inconsistent data between database and Firebase
   */
  User setRole(Long userId, Role role) throws DataAccessException;

  /**
   * Sets a user's role to exactly one role, in the database and on the Firebase server. Clients need to re-authenticate (login again) since
   * this operation revokes all refresh tokens associated with the corresponding user.
   *
   * @param firebaseId the id of the user whose role should be set
   * @param role       the role userId should receive
   * @return the modified user
   * @throws DataNotFoundException if the database does not contain a user with firebase ID {@code firebaseId}
   * @throws DataAccessException   if an error occurs during user lookup, e.g. inconsistent data between database and Firebase
   */
  User setRole(String firebaseId, Role role) throws DataAccessException;

  /**
   * Removes a user's role in the database and clears all roles on the Firebase server. Clients need to re-authenticate (login again) since
   * this operation revokes all refresh tokens associated with the corresponding user.
   *
   * @param firebaseId the id of the user whose role should be removed
   * @return the modified user
   * @throws DataNotFoundException if the database does not contain a user with firebase ID {@code firebaseId}
   * @throws DataAccessException   if an error occurs during user lookup, e.g. inconsistent data between database and Firebase
   */
  User removeRole(String firebaseId) throws DataAccessException;

  /**
   * Finds a user by its database identifier.
   *
   * @param userId the database identifier (primary key) of the user to be found
   * @return the user object corresponding to {@code userId}
   * @throws DataNotFoundException if the database does not contain a user with primary key {@code userId}
   * @throws DataAccessException   if an error occurs during user lookup, e.g. inconsistent data between database and Firebase
   */
  User findById(Long userId) throws DataAccessException;

  /**
   * Finds a user by its Firebase identifier.
   *
   * @param firebaseId the Firebase of the user to be found
   * @return the user object corresponding to {@code firebaseId}
   * @throws DataNotFoundException if the database does not contain a user with Firebase ID {@code firebaseId}
   * @throws DataAccessException   if an error occurs during user lookup, e.g. inconsistent data between database and Firebase
   */
  User findByFirebaseId(String firebaseId) throws DataAccessException;

  /**
   * Finds a user by its email.
   *
   * @param email the email of the user to be found
   * @return the user object corresponding to {@code email}
   * @throws DataNotFoundException if the database does not contain a user with email {@code email}
   * @throws DataAccessException   if an error occurs during user lookup, e.g. inconsistent data between database and Firebase
   */
  User findByEmail(String email) throws DataAccessException;
}
