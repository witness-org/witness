package com.witness.server.service.impl;

import com.witness.server.entity.User;
import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.ServerError;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.repository.UserRepository;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.TimeService;
import com.witness.server.service.UserService;
import com.witness.server.util.ThrowingSupplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final TimeService timeService;
  private final FirebaseService firebaseService;

  @Autowired
  public UserServiceImpl(UserRepository userRepository, TimeService timeService, FirebaseService firebaseService) {
    this.userRepository = userRepository;
    this.timeService = timeService;
    this.firebaseService = firebaseService;
  }

  @Override
  public User createUser(User user, String password) throws DataCreationException, DataNotFoundException, DataModificationException {
    log.info(String.format("Creating user with username \"%s\".", user.getUsername()));
    var firebaseUser = firebaseService.createUser(user.getEmail(), password);

    if (user.getRole() != null) {
      firebaseService.setRole(firebaseUser.getUid(), user.getRole());
    }

    var timestamp = timeService.getCurrentTime();
    var userToPersist = user.toBuilder()
        .id(null)
        .firebaseId(firebaseUser.getUid())
        .createdAt(timestamp)
        .modifiedAt(timestamp)
        .build();

    return userRepository.save(userToPersist);
  }

  @Override
  public User setRole(Long userId, Role role) throws DataAccessException {
    log.info(String.format("Setting role of user with user ID \"%s\" to \"%s\".", userId, role));
    return setRoleInternal(() -> findById(userId), role);
  }

  @Override
  public User setRole(String firebaseId, Role role) throws DataAccessException {
    log.info(String.format("Setting role of user with Firebase ID \"%s\" to \"%s\".", firebaseId, role));
    return setRoleInternal(() -> findByFirebaseId(firebaseId), role);
  }

  @Override
  public User removeRole(String firebaseId) throws DataAccessException {
    log.info(String.format("Removing role from user with Firebase ID \"%s\".", firebaseId));
    return setRoleInternal(() -> findByFirebaseId(firebaseId), null);
  }

  @Override
  public User findById(Long userId) throws DataAccessException {
    log.info(String.format("Trying to find user with user ID %d.", userId));
    return findUserInternal(() ->
        userRepository
            .findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Could not find user with ID \"%s\"".formatted(userId), ServerError.USER_NOT_FOUND)));
  }

  @Override
  public User findByFirebaseId(String firebaseId) throws DataAccessException {
    log.info(String.format("Trying to find user with Firebase ID %s.", firebaseId));
    return findUserInternal(() ->
        userRepository
            .findByFirebaseIdEquals(firebaseId)
            .orElseThrow(() ->
                new DataNotFoundException("Could not find user with firebase ID \"%s\"".formatted(firebaseId), ServerError.USER_NOT_FOUND)));
  }

  @Override
  public User findByEmail(String email) throws DataAccessException {
    log.info(String.format("Trying to find user with email address \"%s\".", email));
    return findUserInternal(() ->
        userRepository
            .findByEmailEqualsIgnoreCase(email)
            .orElseThrow(() -> new DataNotFoundException("Could not find user with email \"%s\"".formatted(email), ServerError.USER_NOT_FOUND)));
  }

  private User setRoleInternal(ThrowingSupplier<User, DataAccessException> userSupplier, Role role) throws DataAccessException {
    var databaseUser = userSupplier.get();

    if (role != null) {
      firebaseService.setRole(databaseUser.getFirebaseId(), role);
    } else {
      firebaseService.clearRoles(databaseUser.getFirebaseId());
    }

    var modifiedDatabaseUser = databaseUser
        .toBuilder()
        .role(role)
        .build();

    return userRepository.save(modifiedDatabaseUser);
  }

  private User findUserInternal(ThrowingSupplier<User, DataNotFoundException> userSupplier) throws DataAccessException {
    var databaseUser = userSupplier.get();
    checkConsistency(databaseUser);
    return databaseUser;
  }

  private void checkConsistency(User databaseUser) throws DataAccessException {
    // data must be consistent, i.e. the persisted Firebase user ID must point to an existing user
    var firebaseId = databaseUser.getFirebaseId();
    var firebaseUser = firebaseService.findUserById(firebaseId);
    if (!databaseUser.getEmail().equals(firebaseUser.getEmail())) {
      log.error("An error occurred while checking if the data are consistent.");
      throw new DataAccessException("Email for user with id \"%s\" deposited in the database does not match Firebase server".formatted(firebaseId));
    }
  }
}
