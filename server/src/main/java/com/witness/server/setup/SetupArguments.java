package com.witness.server.setup;

import com.witness.server.enumeration.LoggingType;
import com.witness.server.enumeration.MuscleGroup;
import com.witness.server.enumeration.Role;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Provides information about users and roles that should be created or assigned, locally and/or at Firebase side, and initial exercises during
 * environment initialization.
 * To be used with "setup" profile.
 *
 * @see EnvironmentBootstrapper
 */
@Component
@ConfigurationProperties(prefix = "setup")
@Data
public class SetupArguments {
  private SetupUserHolder<SetupNewUser> createLocalAndFirebase;
  private SetupUserHolder<SetupFirebaseUser> createLocalWithFirebaseId;
  private SetupUserHolder<SetupFirebaseUser> createLocalWithFirebaseIdAndSetFirebaseRole;
  private SetupExerciseHolder<SetupExercise> createExercises;

  /**
   * Generic POJO representation of data in {@code setup.create-local-and-firebase}, {@code setup.create-local-with-firebase-id} and
   * {@code setup.create-local-with-firebase-id-and-set-firebase-role} sections in the application properties of the {@code setup} profile. Provides
   * one field per possible user role.
   *
   * @param <T> concrete type of users the holder represents
   */
  @Data
  public static class SetupUserHolder<T extends SetupUser> {
    private List<T> admin;
    private List<T> premium;
    private List<T> regular;

    List<T> getUsers(Role role) {
      if (role == null) {
        return regular;
      }

      return switch (role) {
        case ADMIN -> admin;
        case PREMIUM -> premium;
      };
    }
  }

  /**
   * Generic POJO representation of data in {@code setup.create-exercises} sections in the application properties of the {@code setup} profile.
   *
   * @param <T> type of exercises the holder represents
   */
  @Data
  public static class SetupExerciseHolder<T> {
    private List<T> exercises;
  }

  /**
   * Abstract base class for types of users that may be created during setup.
   */
  @Data
  public abstract static class SetupUser {
    protected String email;
  }

  /**
   * A user that is newly created locally and also remotely on the Firebase authentication server.
   */
  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class SetupNewUser extends SetupUser {
    private String password;
  }

  /**
   * A user that is newly created locally and linked with an already existing Firebase authentication by means of its Firebase user ID.
   */
  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class SetupFirebaseUser extends SetupUser {
    private String firebaseId;
  }

  /**
   * An exercise that is created during environment setup.
   */
  @Data
  @EqualsAndHashCode
  public static class SetupExercise {
    private String name;
    private String description;
    private List<MuscleGroup> muscleGroups;
    private List<LoggingType> loggingTypes;
  }
}
