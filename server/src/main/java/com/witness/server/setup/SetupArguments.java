package com.witness.server.setup;

import com.witness.server.enumeration.Role;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Provides information about users and roles that should be created or assigned, locally and/or at Firebase side, during environment initialization.
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

  @Data
  public static class SetupUserHolder<T> {
    private List<T> admin;
    private List<T> premium;
    private List<T> regular;

    List<T> getUsers(Role role) {
      if (role == null) {
        return regular;
      }

      //noinspection EnhancedSwitchMigration (checkstyle does not support 'new' switch syntax)
      switch (role) {
        case ADMIN:
          return admin;
        case PREMIUM:
          return premium;
        default:
          throw new IllegalStateException("Unexpected value: " + role);
      }
    }
  }

  @Data
  public abstract static class SetupUser {
    protected String email;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class SetupNewUser extends SetupUser {
    private String password;
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  public static class SetupFirebaseUser extends SetupUser {
    private String firebaseId;
  }
}
