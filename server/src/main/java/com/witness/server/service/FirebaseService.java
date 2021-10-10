package com.witness.server.service;

import com.witness.server.enumeration.Role;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;

/**
 * Provides methods related to interacting with the Firebase Authentication server.
 */
public interface FirebaseService {

  /**
   * Finds a Firebase user by its ID.
   *
   * @param userId the ID of the Firebase user to look up
   * @return the {@link FirebaseUser} instance corresponding to ID {@code userId}
   * @throws DataNotFoundException if no user with ID {@code userId} can be found
   * @throws DataAccessException   if an error occurs during user lookup
   */
  FirebaseUser findUserById(String userId) throws DataAccessException;

  /**
   * Finds a Firebase user by its email.
   *
   * @param email the email of the Firebase user to look up
   * @return the {@link FirebaseUser} instance corresponding to email {@code email}
   * @throws DataNotFoundException if no user with email {@code email} can be found
   * @throws DataAccessException   if an error occurs during user lookup
   */
  FirebaseUser findUserByEmail(String email) throws DataAccessException;

  /**
   * Creates a new user record on the Firebase server.
   *
   * @param email    the email address of the user to create
   * @param password the password which should be used to login with the new user. Is not stored in the application context, but only transmitted
   *                 to Firebase for the purpose of creating the new user.
   * @return a {@link FirebaseUser} user representing the newly created user. The {@link FirebaseUser#getIssuer()} property is empty because
   *     registration does not go along with authorization, i.e. clients have to obtain an ID token themselves in order to login after signing up.
   * @throws DataCreationException if an error occurs while creating the user account. See {@link DataCreationException#getCause()} for more
   *                               information.
   */
  FirebaseUser createUser(String email, String password) throws DataCreationException;

  /**
   * Verifies a given ID token, i.e. ensures that the token is correctly signed, has not expired, and it was issued to the Firebase project
   * associated with this application. If the parameter {@code checkTokenRevoked} is {@code true}, this method also checks whether if the associated
   * refresh token has been revoked or if the associated user is disabled.
   *
   * @param idToken           the ID token to validate
   * @param checkTokenRevoked specifies whether it should also be checked if the associated refresh token has been revoked or if the associated user
   *                          is disabled
   * @return If the token specified by {@code idToken} is valid, a {@link Credentials} object containing the initial token ({@code idToken}) and
   *     decoded representation of the JWT is returned. If {@code idToken} is {@code null} or empty, {@code null} is returned.
   * @throws AuthenticationException if the token verification failed, see {@link AuthenticationException#getCause()} for detailed information.
   */
  Credentials verifyToken(String idToken, boolean checkTokenRevoked) throws AuthenticationException;

  /**
   * Revokes all refresh tokens for the user specified by {@code userId}. While this will revoke all sessions for a specified user and disable any
   * new ID tokens for existing sessions from getting minted, existing ID tokens may remain active until their natural expiration (one hour). To
   * verify that ID tokens are revoked, use {@link FirebaseService#verifyToken(String, boolean)} with the second argument being {@code true}.
   *
   * @param userId the Firebase ID of the user whose refresh tokens should be invalidated
   * @throws DataModificationException if an error occurs while revoking the refresh tokens. See {@link DataModificationException#getCause()} for
   *                                   more information.
   */
  void revokeRefreshTokens(String userId) throws DataModificationException;

  /**
   * Adds a role to a Firebase user by means of a custom claim. Since this operation amends the privileges of the affected user, it also revokes all
   * refresh tokens associated with {@code userId}. This means the user, on the client-side, has to sign in anew in order to receive new valid tokens.
   *
   * @param userId the ID of the user who should receive a new role
   * @param role   the role to add to {@code userId}'s roles
   * @throws DataModificationException if the identifier of {@code Role} is not contained in the globally configured valid application roles or an
   *                                   error occurs during role addition or token revocation. For the latter two cases, see
   *                                   {@link DataModificationException#getCause()} for more details.
   * @throws DataNotFoundException     if no user with id {@code userId} is found
   * @see FirebaseService#revokeRefreshTokens(String)
   */
  void addRole(String userId, Role role) throws DataModificationException, DataNotFoundException;


  /**
   * Sets a user's role to exactly one role by means of a custom claim. Since this operation amends the privileges of the affected user, it also
   * revokes all refresh tokens associated with {@code userId}. This means the user, on the client-side, has to sign in anew in order to receive new
   * valid tokens.
   *
   * @param userId the ID of the user whose role should be set
   * @param role   the role {@code userId} should receive
   * @throws DataModificationException if the identifier of {@code Role} is not contained in the globally configured valid application roles or an
   *                                   error occurs during role application or token revocation. For the latter two cases, see
   *                                   {@link DataModificationException#getCause()} for more details.
   * @throws DataNotFoundException     if no user with id {@code userId} is found
   * @see FirebaseService#revokeRefreshTokens(String)
   */
  void setRole(String userId, Role role) throws DataModificationException, DataNotFoundException;

  /**
   * Removes a role from a Firebase user's custom claims. Since this operation amends the privileges of the affected user, it also revokes all
   * refresh tokens associated with {@code userId}. This means the user, on the client-side, has to sign in anew in order to receive new valid tokens.
   *
   * @param userId the ID of the user from which a role should be removed
   * @param role   the role to remove from {@code userId}'s roles
   * @throws DataModificationException if the {@link Role#identifier()} of {@code Role} is not contained in the globally configured valid application
   *                                   roles or an error occurs during role removal or token revocation. For the latter two cases, see
   *                                   {@link DataModificationException#getCause()} for more details.
   * @throws DataNotFoundException     if no user with id {@code userId} is found
   * @see FirebaseService#revokeRefreshTokens(String)
   */
  void removeRole(String userId, Role role) throws DataModificationException, DataNotFoundException;


  /**
   * Clears all roles from a Firebase user's custom claims. Since this operation amends the privileges of the affected user, it also revokes all
   * refresh tokens associated with {@code userId}. This means the user, on the client-side, has to sign in anew in order to receive new valid tokens.
   *
   * @param userId the ID of the user whose roles should be cleared
   * @throws DataModificationException if an error occurs during role removal or token revocation. See {@link DataModificationException#getCause()}
   *                                   for more details.
   * @throws DataNotFoundException     if no user with id {@code userId} is found
   * @see FirebaseService#revokeRefreshTokens(String)
   */
  void clearRoles(String userId) throws DataModificationException, DataNotFoundException;
}
