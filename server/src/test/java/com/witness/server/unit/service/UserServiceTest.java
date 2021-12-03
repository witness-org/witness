package com.witness.server.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.witness.server.entity.user.User;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.repository.UserRepository;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.TimeService;
import com.witness.server.service.UserService;
import com.witness.server.service.impl.TimeServiceImpl;
import com.witness.server.service.impl.UserServiceImpl;
import com.witness.server.unit.BaseUnitTest;
import com.witness.server.util.FirebaseServiceMocks;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

@SpringBootTest(classes = {UserServiceImpl.class, TimeServiceImpl.class})
class UserServiceTest extends BaseUnitTest {
  private static final String DATA_ROOT = "data/unit/service/user-service-test/";

  @Autowired
  private UserService userService;

  @MockBean
  private FirebaseService firebaseService;

  @MockBean
  private UserRepository userRepository;

  @SpyBean
  private TimeService timeService;

  @Captor
  private ArgumentCaptor<User> userArgumentCaptor;

  //region createUser

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "CreateUserSpecification1.json", type = CreateUserTestSpecification.class)
  })
  void createUser_withRole_correctUserCreatedAndRoleSet(CreateUserTestSpecification specification)
      throws DataNotFoundException, DataModificationException, DataCreationException {
    when(userRepository.save(any(User.class))).thenReturn(specification.createdUser);
    FirebaseServiceMocks.mockFirebaseServiceCreateUser(firebaseService, specification.createdUser.getFirebaseId());

    var createdUser = userService.createUser(specification.userToCreate, specification.password);

    assertThat(createdUser).isEqualTo(specification.createdUser);

    verify(firebaseService, times(1)).createUser(specification.userToCreate.getEmail(), specification.password);
    verify(firebaseService, times(1)).setRole(specification.createdUser.getFirebaseId(), specification.userToCreate.getRole());
    verify(timeService, times(1)).getCurrentTime();
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getUsername()).isEqualTo(specification.userToCreate.getUsername());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "CreateUserSpecification2.json", type = CreateUserTestSpecification.class)
  })
  void createUser_withRole_correctUserCreatedAndRoleNotSet(CreateUserTestSpecification specification)
      throws DataNotFoundException, DataModificationException, DataCreationException {
    when(userRepository.save(any(User.class))).thenReturn(specification.createdUser);
    FirebaseServiceMocks.mockFirebaseServiceCreateUser(firebaseService, specification.createdUser.getFirebaseId());

    var createdUser = userService.createUser(specification.userToCreate, specification.password);

    assertThat(createdUser).isEqualTo(specification.createdUser);

    verify(firebaseService, times(1)).createUser(specification.userToCreate.getEmail(), specification.password);
    verify(firebaseService, times(0)).setRole(anyString(), any(Role.class));
    verify(timeService, times(1)).getCurrentTime();
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getUsername()).isEqualTo(specification.userToCreate.getUsername());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "CreateUserSpecification1.json", type = CreateUserTestSpecification.class)
  })
  void createUser_firebaseCreateFails_throwException(CreateUserTestSpecification specification) {
    FirebaseServiceMocks.mockFirebaseServiceCreateUserThrows(firebaseService);

    assertThatThrownBy(() -> userService.createUser(specification.userToCreate, specification.password))
        .isInstanceOf(DataCreationException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "CreateUserSpecification1.json", type = CreateUserTestSpecification.class)
  })
  void createUser_firebaseSetRoleFails_throwException(CreateUserTestSpecification specification) {
    FirebaseServiceMocks.mockFirebaseServiceCreateUser(firebaseService, specification.createdUser.getFirebaseId());
    FirebaseServiceMocks.mockFirebaseServiceSetRoleThrows(firebaseService);

    assertThatThrownBy(() -> userService.createUser(specification.userToCreate, specification.password))
        .isInstanceOf(DataNotFoundException.class);
  }

  //endregion

  //region setRole

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void setRole_givenFirebaseId_returnUserWithNewRole(User persistedUser) throws DataAccessException {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    when(userRepository.findByFirebaseIdEquals(persistedUser.getFirebaseId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));
    var newRole = Role.ADMIN;

    var userWithRole = userService.setRole(persistedUser.getFirebaseId(), newRole);

    assertThat(userWithRole)
        .usingRecursiveComparison()
        .ignoringFields("role", "modifiedAt")
        .isEqualTo(persistedUser);
    assertThat(userWithRole.getRole()).isEqualTo(newRole);
    assertThat(userWithRole.getModifiedAt()).isAfter(userWithRole.getCreatedAt());

    verify(firebaseService, times(1)).setRole(persistedUser.getFirebaseId(), newRole);
    verify(firebaseService, times(0)).clearRoles(persistedUser.getFirebaseId());
    verify(timeService, times(1)).getCurrentTime();
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getRole()).isEqualTo(newRole);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void setRole_givenDatabaseId_returnUserWithNewRole(User persistedUser) throws DataAccessException {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    when(userRepository.findByFirebaseIdEquals(persistedUser.getFirebaseId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));
    var newRole = Role.ADMIN;

    var userWithRole = userService.setRole(persistedUser.getId(), newRole);

    assertThat(userWithRole)
        .usingRecursiveComparison()
        .ignoringFields("role", "modifiedAt")
        .isEqualTo(persistedUser);
    assertThat(userWithRole.getRole()).isEqualTo(newRole);
    assertThat(userWithRole.getModifiedAt()).isAfter(userWithRole.getCreatedAt());

    verify(firebaseService, times(1)).setRole(persistedUser.getFirebaseId(), newRole);
    verify(firebaseService, times(0)).clearRoles(persistedUser.getFirebaseId());
    verify(timeService, times(1)).getCurrentTime();
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getRole()).isEqualTo(newRole);
  }

  @Test
  void setRole_givenNonExistentFirebaseId_throwException() {
    assertThatThrownBy(() -> userService.setRole("nonExistentId", Role.PREMIUM)).isInstanceOf(DataNotFoundException.class);
  }

  @Test
  void setRole_givenNonExistentDatabaseId_throwException() {
    assertThatThrownBy(() -> userService.setRole(-23234L, Role.PREMIUM)).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void setRole_givenFirebaseIdFirebaseError_throwException(User persistedUser) {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    when(userRepository.findByFirebaseIdEquals(persistedUser.getFirebaseId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());
    FirebaseServiceMocks.mockFirebaseServiceSetRoleThrows(firebaseService, DataModificationException.class);

    assertThatThrownBy(() -> userService.setRole(persistedUser.getFirebaseId(), Role.ADMIN)).isInstanceOf(DataModificationException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void setRole_givenDatabaseIdFirebaseError_throwException(User persistedUser) {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());
    FirebaseServiceMocks.mockFirebaseServiceSetRoleThrows(firebaseService, DataModificationException.class);

    assertThatThrownBy(() -> userService.setRole(persistedUser.getId(), Role.ADMIN)).isInstanceOf(DataModificationException.class);
  }

  //endregion

  //region removeRole

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void removeRole_givenUser_returnUpdatedUserWithoutRole(User persistedUser) throws DataAccessException {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    when(userRepository.findByFirebaseIdEquals(persistedUser.getFirebaseId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0, User.class));

    var userWithRole = userService.removeRole(persistedUser.getFirebaseId());

    assertThat(userWithRole)
        .usingRecursiveComparison()
        .ignoringFields("role", "modifiedAt")
        .isEqualTo(persistedUser);
    assertThat(userWithRole.getRole()).isNull();
    assertThat(userWithRole.getModifiedAt()).isAfter(userWithRole.getCreatedAt());

    verify(firebaseService, times(0)).setRole(eq(persistedUser.getFirebaseId()), any(Role.class));
    verify(firebaseService, times(1)).clearRoles(persistedUser.getFirebaseId());
    verify(timeService, times(1)).getCurrentTime();
    verify(userRepository, times(1)).save(userArgumentCaptor.capture());
    assertThat(userArgumentCaptor.getValue().getRole()).isNull();
  }

  @Test
  void removeRole_givenNonExistentId_throwException() {
    assertThatThrownBy(() -> userService.removeRole("nonExistentId")).isInstanceOf(DataNotFoundException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void removeRole_FirebaseError_throwException(User persistedUser) {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    when(userRepository.findByFirebaseIdEquals(persistedUser.getFirebaseId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());
    FirebaseServiceMocks.mockFirebaseServiceClearRolesThrows(firebaseService, DataModificationException.class);

    assertThatThrownBy(() -> userService.removeRole(persistedUser.getFirebaseId())).isInstanceOf(DataModificationException.class);
  }

  //endregion

  //region findById

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void findById_givenPersistedUser_returnUser(User persistedUser) throws DataAccessException {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());

    var foundUser = userService.findById(persistedUser.getId());

    assertThat(foundUser).usingRecursiveComparison().isEqualTo(persistedUser);

    verify(userRepository, times(1)).findById(persistedUser.getId());
    verify(firebaseService, times(1)).findUserById(persistedUser.getFirebaseId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void findById_givenNonConsistentPersistedUser_throwException(User persistedUser) {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), "inconsistent.email@example.com");

    assertThatThrownBy(() -> userService.findById(persistedUser.getId())).isInstanceOf(DataAccessException.class);
  }

  @Test
  void findById_givenNonExistentId_throwException() {
    assertThatThrownBy(() -> userService.findById(-2243L)).isInstanceOf(DataNotFoundException.class);
  }

  //endregion

  //region findByFirebaseId

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void findByFirebaseId_givenPersistedUser_returnUser(User persistedUser) throws DataAccessException {
    when(userRepository.findByFirebaseIdEquals(persistedUser.getFirebaseId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());

    var foundUser = userService.findByFirebaseId(persistedUser.getFirebaseId());

    assertThat(foundUser).usingRecursiveComparison().isEqualTo(persistedUser);

    verify(userRepository, times(1)).findByFirebaseIdEquals(persistedUser.getFirebaseId());
    verify(firebaseService, times(1)).findUserById(persistedUser.getFirebaseId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void findByFirebaseId_givenNonConsistentPersistedUser_throwException(User persistedUser) {
    when(userRepository.findById(persistedUser.getId())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), "inconsistent.email@example.com");

    assertThatThrownBy(() -> userService.findByFirebaseId(persistedUser.getFirebaseId())).isInstanceOf(DataAccessException.class);
  }

  @Test
  void findByFirebaseId_givenNonExistentId_throwException() {
    assertThatThrownBy(() -> userService.findByFirebaseId("nonExistentId")).isInstanceOf(DataNotFoundException.class);
  }

  //endregion

  //region findById

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void findByEmail_givenPersistedUser_returnUser(User persistedUser) throws DataAccessException {
    when(userRepository.findByEmailEqualsIgnoreCase(persistedUser.getEmail())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), persistedUser.getEmail());

    var foundUser = userService.findByEmail(persistedUser.getEmail());

    assertThat(foundUser).usingRecursiveComparison().isEqualTo(persistedUser);

    verify(userRepository, times(1)).findByEmailEqualsIgnoreCase(persistedUser.getEmail());
    verify(firebaseService, times(1)).findUserById(persistedUser.getFirebaseId());
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserPersisted1.json", type = User.class)
  })
  void findByEmail_givenNonConsistentPersistedUser_throwException(User persistedUser) {
    when(userRepository.findByEmailEqualsIgnoreCase(persistedUser.getEmail())).thenReturn(Optional.of(persistedUser));
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, persistedUser.getFirebaseId(), "inconsistent.email@example.com");

    assertThatThrownBy(() -> userService.findByEmail(persistedUser.getEmail())).isInstanceOf(DataAccessException.class);
  }

  @Test
  void findByEmail_givenNonExistentId_throwException() {
    assertThatThrownBy(() -> userService.findByEmail("nonExistingEmail")).isInstanceOf(DataNotFoundException.class);
  }

  //endregion


  @Data
  @NoArgsConstructor
  static class CreateUserTestSpecification {
    private User userToCreate;
    private User createdUser;
    private String password;
  }
}
