package com.witness.server.setup;

import com.witness.server.entity.User;
import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.Sex;
import com.witness.server.repository.UserRepository;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.UserService;
import com.witness.server.util.ThrowingConsumer;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.internal.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Handles environment initialization which consists of local user database preparation as well as synchronization with and adaptation of user
 * records on the Firebase authentication server. Typical tasks are application of roles to user records or linking local user records with
 * Firebase user records by means of the Firebase ID.
 */
@Profile("setup")
@Component
@Slf4j
public class EnvironmentBootstrapper {
  private final SetupArguments setupArguments;
  private final UserService userService;
  private final FirebaseService firebaseService;
  private final UserRepository userRepository;
  private final Random random;

  @Autowired
  public EnvironmentBootstrapper(SetupArguments setupArguments, UserService userService, FirebaseService firebaseService,
                                 UserRepository userRepository) {
    this.firebaseService = firebaseService;
    this.random = new Random();
    this.setupArguments = setupArguments;
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @PostConstruct
  private void initializeEnvironment() {
    log.info("Initializing environment");

    if (setupArguments == null) {
      log.info("Could not read setup arguments, stopping.");
      return;
    }

    var createLocalAndFirebase = setupArguments.getCreateLocalAndFirebase();
    if (createLocalAndFirebase != null) {
      forEachRoleAndRegular(role ->
          createUsers(createLocalAndFirebase.getUsers(role),
              role,
              "Step 1/3, role \"{}\": No users to create locally and on the Firebase server",
              "Step 1/3, role \"{}\": Creating {} users locally and on the Firebase server",
              setupUser -> getDummyUser(setupUser.getEmail(), role, null, false),
              (userToCreate, setupUser) -> createUserLocallyAndInFirebase(userToCreate, setupUser.getPassword())));
    }

    var createLocalWithFirebaseId = setupArguments.getCreateLocalWithFirebaseId();
    if (createLocalWithFirebaseId != null) {
      forEachRoleAndRegular(role ->
          createUsers(createLocalWithFirebaseId.getUsers(role),
              role,
              "Step 2/3, role \"{}\": No users to create locally (link with Firebase)",
              "Step 2/3, role \"{}\": Creating {} users locally (link with Firebase)",
              setupUser -> getDummyUser(setupUser.getEmail(), role, setupUser.getFirebaseId(), true),
              (userToCreate, setupUser) -> createUserLocallyAndLinkWithFirebase(userToCreate)));
    }

    var createLocalWithFirebaseIdAndSetFirebaseRole = setupArguments.getCreateLocalWithFirebaseIdAndSetFirebaseRole();
    if (createLocalWithFirebaseIdAndSetFirebaseRole != null) {
      forEachRoleAndRegular(role ->
          createUsers(createLocalWithFirebaseIdAndSetFirebaseRole.getUsers(role),
              role,
              "Step 3/3, role \"{}\": No users to create locally (link with Firebase and set Firebase role)",
              "Step 3/3, \"{}\": Creating {} users locally (link with Firebase and set Firebase role)",
              setupUser -> getDummyUser(setupUser.getEmail(), role, setupUser.getFirebaseId(), true),
              (userToCreate, setupUser) -> createUserLocallyAndLinkWithFirebaseAndSetFirebaseRole(userToCreate)));
    }

    log.info("Finished environment initialization");
  }

  private <T extends SetupArguments.SetupUser> void createUsers(List<T> users, Role role, String emptyMessage, String creatingMessage,
                                                                Function<T, User> dummyUserGetter, BiConsumer<User, T> userCreator) {
    var roleDescription = role != null ? role.identifier() : "regular";
    if (users == null || users.isEmpty()) {
      log.info(emptyMessage, roleDescription);
      return;
    }

    log.info(creatingMessage, roleDescription, users.size());
    for (var i = 0; i < users.size(); i++) {
      var user = users.get(i);
      log.debug("  {}/{}: user \"{}\"", i + 1, users.size(), user.getEmail());
      var userToCreate = dummyUserGetter.apply(user);
      userCreator.accept(userToCreate, user);
    }
  }

  private void createUserLocallyAndInFirebase(User user, String password) {
    tryCreateUser(user, userToCreate -> userService.createUser(userToCreate, password));
  }

  private void createUserLocallyAndLinkWithFirebase(User user) {
    tryCreateUser(user, userRepository::save);
  }

  private void createUserLocallyAndLinkWithFirebaseAndSetFirebaseRole(User user) {
    tryCreateUser(user, userToCreate -> {
          userRepository.save(userToCreate);
          firebaseService.setRole(userToCreate.getFirebaseId(), userToCreate.getRole());
        }
    );
  }

  private User getDummyUser(String email, Role role, String firebaseId, boolean setTimestamps) {
    var timestamp = ZonedDateTime.now(); // timezone is irrelevant for this dummy object, therefore no TimeService
    return User.builder()
        .email(email)
        .username(email)
        .role(role)
        .firebaseId(firebaseId)
        .sex(random.nextBoolean() ? Sex.FEMALE : Sex.MALE)
        .height((long) random.nextInt(200))
        .createdAt(setTimestamps ? timestamp : null)
        .modifiedAt(setTimestamps ? timestamp : null)
        .build();
  }

  private void tryCreateUser(User user, ThrowingConsumer<User, Exception> userCreator) {
    try {
      userCreator.accept(user);
    } catch (Exception e) {
      log.warn("  Could not create user \"{}\": {}", user.getEmail(), e.getMessage());
    }
  }

  private void forEachRoleAndRegular(Consumer<Role> operation) {
    for (var role : Role.values()) {
      operation.accept(role);
    }
    operation.accept(null); // do not forget regular users
  }
}
