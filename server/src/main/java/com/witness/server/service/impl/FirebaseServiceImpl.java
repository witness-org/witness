package com.witness.server.service.impl;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.witness.server.configuration.SecurityProperties;
import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.ServerError;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.mapper.FirebaseMapper;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;
import com.witness.server.service.FirebaseService;
import com.witness.server.util.ThrowingSupplier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FirebaseServiceImpl implements FirebaseService {
  private final SecurityProperties securityProperties;
  private final FirebaseAuth firebaseAuth;
  private final FirebaseMapper firebaseMapper;

  @Autowired
  public FirebaseServiceImpl(SecurityProperties securityProperties, FirebaseAuth firebaseAuth, FirebaseMapper firebaseMapper) {
    this.securityProperties = securityProperties;
    this.firebaseAuth = firebaseAuth;
    this.firebaseMapper = firebaseMapper;
  }

  @Override
  public FirebaseUser findUserById(String userId) throws DataAccessException {
    log.info(String.format("Trying to find Firebase user with ID %s.", userId));
    return findUserInternal(() -> findUserByIdInternal(userId));
  }

  @Override
  public FirebaseUser findUserByEmail(String email) throws DataAccessException {
    log.info(String.format("Trying to find Firebase user with email address \"%s\".", email));
    return findUserInternal(() -> findUserByEmailInternal(email));
  }

  @Override
  public FirebaseUser createUser(String email, String password) throws DataCreationException {
    log.info(String.format("Creating new Firebase user with email address \"%s\".", email));
    var request = new UserRecord.CreateRequest()
        .setEmail(email)
        .setPassword(password);

    try {
      var userRecord = FirebaseAuth.getInstance().createUser(request);
      return firebaseMapper.recordToUser(userRecord);
    } catch (FirebaseAuthException e) {
      log.error("Creating Firebase user failed.");
      throw new DataCreationException("Could not create Firebase user: %s".formatted(e.getMessage()),
          ServerError.fromFirebaseError(e.getAuthErrorCode(), ServerError.COULD_NOT_CREATE_USER), e);
    }
  }

  @Override
  public Credentials verifyToken(String idToken, boolean checkTokenRevoked) throws AuthenticationException {
    log.info("Verifying token.");
    try {
      var decodedToken = firebaseAuth.verifyIdToken(idToken, checkTokenRevoked);
      return new Credentials(decodedToken, idToken);
    } catch (IllegalArgumentException e) {
      log.warn("The current request does not provide a token to validate.");
      return null;
    } catch (FirebaseAuthException e) {
      log.error("Verifying token failed.");
      throw new AuthenticationException(e.getMessage(), ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
    }
  }

  @Override
  public void revokeRefreshTokens(String userId) throws DataModificationException {
    log.info(String.format("Revoking refresh tokens of user with user ID %s.", userId));
    try {
      FirebaseAuth.getInstance().revokeRefreshTokens(userId);
    } catch (FirebaseAuthException e) {
      log.error("Revoking refresh tokens failed.");
      throw new DataModificationException("Could not revoke token of user %s".formatted(userId),
          ServerError.fromFirebaseError(e.getAuthErrorCode()),
          e);
    }
  }

  @Override
  public void addRole(String userId, Role role) throws DataNotFoundException, DataModificationException {
    log.info(String.format("Adding role \"%s\" to user with user ID %s.", role, userId));
    setOrAddRole(userId, role, false);
  }

  @Override
  public void setRole(String userId, Role role) throws DataModificationException, DataNotFoundException {
    log.info(String.format("Setting role for user with user ID %s: \"%s\".", userId, role));
    setOrAddRole(userId, role, true);
  }

  @Override
  public void removeRole(String userId, Role role) throws DataModificationException, DataNotFoundException {
    log.info(String.format("Removing role \"%s\" from user with user ID %s", role, userId));
    try {
      var user = findUserByIdInternal(userId);
      var customClaims = new HashMap<>(user.getCustomClaims());
      customClaims.remove(role.identifier());
      firebaseAuth.setCustomUserClaims(userId, customClaims);
      revokeRefreshTokens(userId);
    } catch (FirebaseAuthException e) {
      log.error("Removing role \"%s\" from user \"%s\" failed.".formatted(role, userId), e);
      if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
        throw new DataNotFoundException(e.getMessage(), ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
      } else {
        throw new DataModificationException(e.getMessage(), ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
      }
    }
  }

  @Override
  public void clearRoles(String userId) throws DataModificationException, DataNotFoundException {
    try {
      findUserByIdInternal(userId); // to verify userId's existence
      firebaseAuth.setCustomUserClaims(userId, Collections.emptyMap());
      revokeRefreshTokens(userId);
    } catch (FirebaseAuthException e) {
      log.error("Clearing roles of user \"%s\" failed.".formatted(userId), e);
      if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
        throw new DataNotFoundException(e.getMessage(), ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
      } else {
        throw new DataModificationException(e.getMessage(), ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
      }
    }
  }

  private void setOrAddRole(String userId, Role role, boolean set) throws DataModificationException, DataNotFoundException {
    try {
      var user = findUserByIdInternal(userId);
      var validRoles = securityProperties.getValidRoles();

      if (!validRoles.contains(role.identifier())) {
        throw new DataModificationException("Given role is not valid. Allowed roles: %s".formatted(String.join(", ", validRoles)),
            ServerError.INVALID_ROLE);
      }

      Map<String, Object> customClaims;
      if (set) {
        customClaims = Map.of(role.identifier(), true);
      } else {
        customClaims = new HashMap<>(user.getCustomClaims());
        customClaims.putIfAbsent(role.identifier(), true);
      }

      firebaseAuth.setCustomUserClaims(userId, customClaims);
      revokeRefreshTokens(userId);
    } catch (FirebaseAuthException e) {
      log.error("Could not %s role \"%s\" for user \"%s\".".formatted(set ? "set" : "add", role, userId), e);
      if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
        throw new DataNotFoundException(e.getMessage(), ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
      } else {
        throw new DataModificationException(e.getMessage(), ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
      }
    }
  }

  private FirebaseUser findUserInternal(ThrowingSupplier<UserRecord, FirebaseAuthException> userSupplier) throws DataAccessException {
    try {
      var userRecord = userSupplier.get();
      return firebaseMapper.recordToUser(userRecord);
    } catch (FirebaseAuthException e) {
      if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
        log.error("The requested user was not found.");
        throw new DataNotFoundException("Firebase user not found: %s".formatted(e.getMessage()),
            ServerError.fromFirebaseError(e.getAuthErrorCode()), e);
      } else {
        log.error("An unexpected error occurred while looking up user.");
        throw new DataAccessException("Unexpected error while looking up user",
            ServerError.fromFirebaseError(e.getAuthErrorCode(), ServerError.LOOKUP_FAILURE), e);
      }
    }
  }

  private UserRecord findUserByIdInternal(String userId) throws FirebaseAuthException {
    return firebaseAuth.getUser(userId);
  }

  private UserRecord findUserByEmailInternal(String userId) throws FirebaseAuthException {
    return firebaseAuth.getUserByEmail(userId);
  }
}
