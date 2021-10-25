package com.witness.server.integration.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.witness.server.dto.UserCreateDto;
import com.witness.server.dto.UserDto;
import com.witness.server.entity.User;
import com.witness.server.model.FirebaseUser;
import com.witness.server.repository.UserRepository;
import com.witness.server.service.TimeService;
import com.witness.server.util.Comparators;
import com.witness.server.util.FirebaseServiceMocks;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

class UserControllerTest extends BaseControllerIntegrationTest {
  private static final String DATA_ROOT = "data/integration/web/user-controller-test/";

  private static final String REGISTER_USER_URL = "/register";
  private static final String FIND_BY_ID_URL = "/findById/%s";
  private static final String FIND_BY_EMAIL_URL = "/byEmail/%s";
  private static final String SET_ROLE_URL = "/%s/setRole";
  private static final String REMOVE_ROLE_URL = "/%s/removeRole";

  @Autowired
  UserRepository userRepository;

  @SpyBean
  private TimeService timeService;

  @Override
  String getEndpointUrl() {
    return "user";
  }

  //region /registerUser

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtoWithoutRole.json", type = UserCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserDtoWithoutRole.json", type = UserDto.class)
  })
  void registerUser_validUserCreateDtoWithoutRoleNoAuthentication_return201AndCorrectDto(UserCreateDto createDto, UserDto userDto) {
    FirebaseServiceMocks.mockFirebaseServiceCreateUser(firebaseService, userDto.getFirebaseId());
    when(timeService.getCurrentTime()).thenReturn(userDto.getCreatedAt());

    var response = exchange(TestAuthentication.NONE, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, UserDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(userDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtoWithoutRole.json", type = UserCreateDto.class)
  })
  void registerUser_validUserCreateDtoWithoutRoleFirebaseErrorNoAuthentication_return500(UserCreateDto createDto) {
    FirebaseServiceMocks.mockFirebaseServiceCreateUserThrows(firebaseService);

    var response = exchange(TestAuthentication.NONE, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtoWithRole.json", type = UserCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserDtoWithRole.json", type = UserDto.class)
  })
  void registerUser_validUserCreateDtoWithRoleAsAdmin_return201AndCorrectDto(UserCreateDto createDto, UserDto userDto) {
    FirebaseServiceMocks.mockFirebaseServiceCreateUser(firebaseService, userDto.getFirebaseId());
    when(timeService.getCurrentTime()).thenReturn(userDto.getCreatedAt());

    var response = exchange(TestAuthentication.ADMIN, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, UserDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(userDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtoWithRole.json", type = UserCreateDto.class),
      @JsonFileSource(value = DATA_ROOT + "UserDtoWithRole.json", type = UserDto.class)
  })
  void registerUser_validUserCreateDtoWithRoleAsAdmin_return404(UserCreateDto createDto, UserDto userDto) {
    FirebaseServiceMocks.mockFirebaseServiceCreateUser(firebaseService, userDto.getFirebaseId());
    FirebaseServiceMocks.mockFirebaseServiceSetRoleThrows(firebaseService);
    when(timeService.getCurrentTime()).thenReturn(userDto.getCreatedAt());

    var response = exchange(TestAuthentication.ADMIN, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtoWithRole.json", type = UserCreateDto.class)
  })
  void registerUser_validUserCreateDtoWithRoleAsPremium_return403(UserCreateDto createDto) {
    var response = exchange(TestAuthentication.PREMIUM, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtoWithRole.json", type = UserCreateDto.class)
  })
  void registerUser_validUserCreateDtoWithRoleAsRegular_return403(UserCreateDto createDto) {
    var response = exchange(TestAuthentication.REGULAR, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtoWithRole.json", type = UserCreateDto.class)
  })
  void registerUser_validUserCreateDtoWithRoleNoAuthentication_return403(UserCreateDto createDto) {
    var response = exchange(TestAuthentication.NONE, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserCreateDtosWithInvalidProperties.json", type = UserCreateDto[].class)
  })
  void registerUser_invalidUserCreateDtoNoAuthentication_return400(UserCreateDto createDto) {
    var response = exchange(TestAuthentication.NONE, requestUrl(REGISTER_USER_URL), HttpMethod.POST, createDto, Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  //endregion

  //region /findById/{id}

  @ParameterizedTest
  @CsvSource({"NONE,UNAUTHORIZED", "REGULAR,FORBIDDEN", "PREMIUM,FORBIDDEN"})
  void findById_nonePersistedInsufficientPermissions_return401Or403(TestAuthentication authMode, HttpStatus expectedStatusCode) {
    var response = get(authMode, requestUrl(FIND_BY_ID_URL, 1), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
  }

  @ParameterizedTest
  @ValueSource(strings = {"someString", "1.5", "-3"})
  void findById_invalidIdAsAdmin_return400(String invalidId) {
    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_ID_URL, invalidId), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void findById_nonePersistedAsAdmin_return404() {
    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_ID_URL, 1), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UsersPersisted.json", type = User[].class)
  })
  void findById_fourPersistedIdNotFoundAsAdmin_return404(User[] persistedUsers) {
    persistEntities(userRepository, persistedUsers);

    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_ID_URL, 5), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FindByIdInput.json", type = FindByIdTestSpecification.class)
  })
  void findById_fourPersistedIdFoundAsAdmin_return200AndCorrectUserDto(FindByIdTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUsers);
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, specification.firebaseUser.getUid(), specification.firebaseUser);

    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_ID_URL, specification.lookupId), UserDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(specification.expectedDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FindByIdInput.json", type = FindByIdTestSpecification.class)
  })
  void findById_fourPersistedInternalErrorAsAdmin_return500(FindByIdTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUsers);
    FirebaseServiceMocks.mockFirebaseServiceFindUserByIdThrows(firebaseService);

    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_ID_URL, specification.lookupId), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  //endregion

  //region /findByEmail/{email}

  @ParameterizedTest
  @CsvSource({"NONE,UNAUTHORIZED", "REGULAR,FORBIDDEN", "PREMIUM,FORBIDDEN"})
  void findByEmail_nonePersistedInsufficientPermissions_return401Or403(TestAuthentication authMode, HttpStatus expectedStatusCode) {
    var response = get(authMode, requestUrl(FIND_BY_EMAIL_URL, "some@email.com"), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
  }

  @ParameterizedTest
  @ValueSource(strings = {"someString", "@@@", "@test.com", "test@", "test@test@test.com", "1", "-3", "2.3"})
  void findByEmail_invalidEmailAsAdmin_return400(String invalidId) {
    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_EMAIL_URL, invalidId), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void findByEmail_nonePersistedAsAdmin_return404() {
    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_EMAIL_URL, "does.not.exist@test.com"), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UsersPersisted.json", type = User[].class)
  })
  void findByEmail_fourPersistedIdNotFoundAsAdmin_return404(User[] persistedUsers) {
    persistEntities(userRepository, persistedUsers);

    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_EMAIL_URL, "does.not.exist@test.com"), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FindByEmailInput.json", type = FindByEmailTestSpecification.class)
  })
  void findByEmail_fourPersistedIdFoundAsAdmin_return200AndCorrectUserDto(FindByEmailTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUsers);
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, specification.firebaseUser.getUid(), specification.firebaseUser);

    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_EMAIL_URL, specification.lookupEmail), UserDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .isEqualTo(specification.expectedDto);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FindByEmailInput.json", type = FindByEmailTestSpecification.class)
  })
  void findByEmail_fourPersistedInternalErrorAsAdmin_return500(FindByEmailTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUsers);
    FirebaseServiceMocks.mockFirebaseServiceFindUserByIdThrows(firebaseService);

    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_EMAIL_URL, specification.lookupEmail), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FindByEmailInput.json", type = FindByEmailTestSpecification.class)
  })
  void findByEmail_fourPersistedInconsistentAsAdmin_return500(FindByEmailTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUsers);
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, specification.firebaseUser.getUid(),
        specification.firebaseUser.toBuilder().email("anotherUser@example.com").build());

    var response = get(TestAuthentication.ADMIN, requestUrl(FIND_BY_EMAIL_URL, specification.lookupEmail), Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  //endregion

  //region /{id}/setRole

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetRoleInput.json", type = RoleModificationTestSpecification[].class)
  })
  void setRole_existingIdAsAdmin_return200AndModifiedUser(RoleModificationTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUser);
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, specification.firebaseUser.getUid(), specification.firebaseUser);

    var params = toMultiValueMap(Map.of("role", specification.expectedUserDto.getRole().name()));
    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(SET_ROLE_URL, specification.persistedUser.getFirebaseId()),
        HttpMethod.PATCH,
        params,
        UserDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .ignoringFields("modifiedAt")
        .isEqualTo(specification.expectedUserDto);
    assertThat(response.getBody().getModifiedAt()).isNotEqualTo(specification.expectedUserDto.getModifiedAt());
  }

  @ParameterizedTest
  @CsvSource({"NONE,UNAUTHORIZED", "REGULAR,FORBIDDEN", "PREMIUM,FORBIDDEN"})
  void setRole_insufficientPermissions_return401Or403(TestAuthentication authMode, HttpStatus expectedStatusCode) {
    var params = toMultiValueMap(Map.of("role", "ADMIN"));
    var response = exchange(authMode,
        requestUrl(SET_ROLE_URL, "anyFirebaseUserId"),
        HttpMethod.PATCH,
        params,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetRoleSingleInput.json", type = RoleModificationTestSpecification.class)
  })
  void setRole_nonExistingIdAsAdmin_return404(RoleModificationTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUser);
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, specification.firebaseUser.getUid(), specification.firebaseUser);

    var params = toMultiValueMap(Map.of("role", specification.expectedUserDto.getRole().name()));
    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(SET_ROLE_URL, "nonExistingFirebaseUserId"),
        HttpMethod.PATCH,
        params,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"admin", "premium", "invalidRole", "ROLE_ADMIN", "ROLE_PREMIUM", "      "})
  void setRole_invalidRoleSpecification_return400(String roleQueryParameter) {
    // We cannot use Map.of() here because that does not support null values. Since @NullAndEmptySource is in place, we create the map differently.
    var params = toMultiValueMap(Collections.singletonMap("role", roleQueryParameter));

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(SET_ROLE_URL, "nonExistingFirebaseUserId"),
        HttpMethod.PATCH,
        params,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"     "})
  void setRole_invalidIdSpecification_return4xx(String firebaseId) {
    // We cannot use Map.of() here because that does not support null values. Since @NullAndEmptySource is in place, we create the map differently.
    var params = toMultiValueMap(Map.of("role", "ADMIN"));

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(SET_ROLE_URL, firebaseId),
        HttpMethod.PATCH,
        params,
        Object.class);

    assertThat(response.getStatusCode().is4xxClientError()).isTrue();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetRoleSingleInput.json", type = RoleModificationTestSpecification.class)
  })
  void setRole_existingIdAsAdminInternalError_return500(RoleModificationTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUser);
    FirebaseServiceMocks.mockFirebaseServiceFindUserByIdThrows(firebaseService);

    var params = toMultiValueMap(Map.of("role", specification.expectedUserDto.getRole().name()));
    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(SET_ROLE_URL, specification.persistedUser.getFirebaseId()),
        HttpMethod.PATCH,
        params,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  //endregion

  //region /{id}/removeRole

  @ParameterizedTest
  @JsonFileSources(unwrapArrays = true, parameters = {
      @JsonFileSource(value = DATA_ROOT + "RemoveRoleInput.json", type = RoleModificationTestSpecification[].class)
  })
  void removeRole_existingIdAsAdmin_return200AndModifiedUser(RoleModificationTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUser);
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, specification.firebaseUser.getUid(), specification.firebaseUser);

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(REMOVE_ROLE_URL, specification.persistedUser.getFirebaseId()),
        HttpMethod.PATCH,
        UserDto.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody())
        .usingRecursiveComparison()
        .withComparatorForType(Comparators.ZONED_DATE_TIME_COMPARATOR, ZonedDateTime.class)
        .ignoringFields("modifiedAt")
        .isEqualTo(specification.expectedUserDto);
    assertThat(response.getBody().getModifiedAt()).isNotEqualTo(specification.expectedUserDto.getModifiedAt());
  }

  @ParameterizedTest
  @CsvSource({"NONE,UNAUTHORIZED", "REGULAR,FORBIDDEN", "PREMIUM,FORBIDDEN"})
  void removeRole_insufficientPermissions_return401Or403(TestAuthentication authMode, HttpStatus expectedStatusCode) {
    var response = exchange(authMode,
        requestUrl(REMOVE_ROLE_URL, "anyFirebaseUserId"),
        HttpMethod.PATCH,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"     "})
  void removeRole_invalidIdSpecification_return4xx(String firebaseId) {
    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(REMOVE_ROLE_URL, firebaseId),
        HttpMethod.PATCH,
        Object.class);

    assertThat(response.getStatusCode().is4xxClientError()).isTrue();
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetRoleSingleInput.json", type = RoleModificationTestSpecification.class)
  })
  void removeRole_nonExistingIdAsAdmin_return404(RoleModificationTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUser);
    FirebaseServiceMocks.mockFirebaseServiceFindUserById(firebaseService, specification.firebaseUser.getUid(), specification.firebaseUser);

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(REMOVE_ROLE_URL, "nonExistingFirebaseUserId"),
        HttpMethod.PATCH,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "SetRoleSingleInput.json", type = RoleModificationTestSpecification.class)
  })
  void removeRole_existingIdAsAdminInternalError_return500(RoleModificationTestSpecification specification) {
    persistEntities(userRepository, specification.persistedUser);
    FirebaseServiceMocks.mockFirebaseServiceFindUserByIdThrows(firebaseService);

    var response = exchange(TestAuthentication.ADMIN,
        requestUrl(REMOVE_ROLE_URL, specification.persistedUser.getFirebaseId()),
        HttpMethod.PATCH,
        Object.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  //endregion


  @Data
  @NoArgsConstructor
  static class FindByIdTestSpecification {
    private List<User> persistedUsers;
    private Long lookupId;
    private FirebaseUser firebaseUser;
    private UserDto expectedDto;
  }

  @Data
  @NoArgsConstructor
  static class FindByEmailTestSpecification {
    private List<User> persistedUsers;
    private String lookupEmail;
    private FirebaseUser firebaseUser;
    private UserDto expectedDto;
  }

  @Data
  @NoArgsConstructor
  static class RoleModificationTestSpecification {
    private User persistedUser;
    private FirebaseUser firebaseUser;
    private UserDto expectedUserDto;
  }
}
