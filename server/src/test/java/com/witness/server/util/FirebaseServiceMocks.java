package com.witness.server.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.witness.server.enumeration.Role;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.model.FirebaseUser;
import com.witness.server.service.FirebaseService;
import lombok.SneakyThrows;

/**
 * Encapsulates methods that fulfill common mocking requirements in order to control the behaviour of external Firebase services during tests.
 */
public final class FirebaseServiceMocks {
  private FirebaseServiceMocks() {
  }

  /**
   * Mocks the {@link FirebaseService#createUser(String, String)} by returning a {@link FirebaseUser} with custom Firebase ID.
   *
   * @param firebaseService    the {@link FirebaseService} instance to mock
   * @param expectedFirebaseId the firebase ID of the {@link FirebaseUser} to be returned by {@link FirebaseService#createUser(String, String)}
   */
  @SneakyThrows(DataCreationException.class)
  public static void mockFirebaseServiceCreateUser(FirebaseService firebaseService, String expectedFirebaseId) {
    when(firebaseService.createUser(anyString(), anyString()))
        .thenAnswer(invocation -> {
              var userEmail = invocation.getArgument(0, String.class);
              return FirebaseUser.builder()
                  .name(userEmail)
                  .email(userEmail)
                  .uid(expectedFirebaseId)
                  .isEmailVerified(true)
                  .issuer("MockedFirebaseUserIssuer")
                  .picture("MockedFirebaseUserPictureUrl")
                  .build();
            }
        );
  }

  /**
   * Mocks the {@link FirebaseService#findUserById(String)} by returning a custom {@link FirebaseUser}.
   *
   * @param firebaseService the {@link FirebaseService} instance to mock
   * @param firebaseId      the id for which {@code returnUser} should be returned
   * @param returnUser      the {@link FirebaseUser} instance to return when invoking {@link FirebaseService#findUserById(String)} with
   *                        {@code firebaseId}
   */
  @SneakyThrows({DataNotFoundException.class, DataAccessException.class})
  public static void mockFirebaseServiceFindUserById(FirebaseService firebaseService, String firebaseId, FirebaseUser returnUser) {
    when(firebaseService.findUserById(firebaseId)).thenReturn(returnUser);
  }

  /**
   * Mocks the {@link FirebaseService#findUserById(String)} by returning a dummy {@link FirebaseUser} with custom email.
   *
   * @param firebaseService the {@link FirebaseService} instance to mock
   * @param firebaseId      the id for which {@code returnUser} should be returned
   * @param email           the email address of the dummy {@link FirebaseUser} instance to return when invoking
   *                        {@link FirebaseService#findUserById(String)} with {@code firebaseId}
   */
  @SneakyThrows({DataNotFoundException.class, DataAccessException.class})
  public static void mockFirebaseServiceFindUserById(FirebaseService firebaseService, String firebaseId, String email) {
    when(firebaseService.findUserById(firebaseId)).thenReturn(FirebaseUser.builder()
        .name(email)
        .email(email)
        .uid(firebaseId)
        .isEmailVerified(true)
        .issuer("MockedFirebaseUserIssuer")
        .picture("MockedFirebaseUserPictureUrl")
        .build());
  }

  /**
   * Mocks {@link FirebaseService#findUserById(String)}} to throw a {@link DataAccessException}.
   *
   * @param firebaseService the {@link FirebaseService} instance to mock
   */
  @SneakyThrows(DataAccessException.class)
  public static void mockFirebaseServiceFindUserByIdThrows(FirebaseService firebaseService) {
    doThrow(DataAccessException.class).when(firebaseService).findUserById(anyString());
  }

  /**
   * Mocks {@link FirebaseService#createUser(String, String)} to throw a {@link DataCreationException}.
   *
   * @param firebaseService the {@link FirebaseService} instance to mock
   */
  @SneakyThrows(DataCreationException.class)
  public static void mockFirebaseServiceCreateUserThrows(FirebaseService firebaseService) {
    when(firebaseService.createUser(anyString(), anyString())).thenThrow(DataCreationException.class);
  }

  /**
   * Mocks {@link FirebaseService#setRole(String, Role)} to throw a {@link DataNotFoundException}.
   *
   * @param firebaseService the {@link FirebaseService} instance to mock
   */
  public static void mockFirebaseServiceSetRoleThrows(FirebaseService firebaseService) {
    mockFirebaseServiceSetRoleThrows(firebaseService, DataNotFoundException.class);
  }

  /**
   * Mocks {@link FirebaseService#setRole(String, Role)} to throw a specifiable exception.
   *
   * @param firebaseService the {@link FirebaseService} instance to mock
   * @param exceptionClass  the {@link Throwable} to be thrown when invoking {@link FirebaseService#setRole(String, Role)}
   */
  @SneakyThrows({DataModificationException.class, DataNotFoundException.class})
  public static void mockFirebaseServiceSetRoleThrows(FirebaseService firebaseService, Class<? extends Throwable> exceptionClass) {
    doThrow(exceptionClass).when(firebaseService).setRole(anyString(), any(Role.class));
  }

  /**
   * Mocks {@link FirebaseService#clearRoles(String)}} to throw a specifiable exception.
   *
   * @param firebaseService the {@link FirebaseService} instance to mock
   * @param exceptionClass  the {@link Throwable} to be thrown when invoking {@link FirebaseService#clearRoles(String)}
   */
  @SneakyThrows({DataModificationException.class, DataNotFoundException.class})
  public static void mockFirebaseServiceClearRolesThrows(FirebaseService firebaseService, Class<? extends Throwable> exceptionClass) {
    doThrow(exceptionClass).when(firebaseService).clearRoles(anyString());
  }
}
