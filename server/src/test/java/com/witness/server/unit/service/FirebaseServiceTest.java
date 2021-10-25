package com.witness.server.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.ErrorCode;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.witness.server.configuration.SecurityProperties;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.exception.DataAccessException;
import com.witness.server.exception.DataCreationException;
import com.witness.server.exception.DataModificationException;
import com.witness.server.exception.DataNotFoundException;
import com.witness.server.mapper.FirebaseMapperImpl;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.impl.FirebaseServiceImpl;
import com.witness.server.util.JsonFileSource;
import com.witness.server.util.JsonFileSources;
import com.witness.server.util.converters.FirebaseTokenArgumentConverter;
import com.witness.server.util.converters.UserRecordArgumentConverter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {FirebaseServiceImpl.class, SecurityProperties.class, FirebaseMapperImpl.class})
class FirebaseServiceTest {
  private static final String DATA_ROOT = "data/unit/service/firebase-service-test/";

  @Autowired
  private FirebaseService firebaseService;

  @MockBean
  FirebaseAuth firebaseAuth;

  @MockBean
  SecurityProperties securityProperties;

  @Captor
  ArgumentCaptor<Map<String, Object>> customClaimMapCaptor;

  //region findUserById

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FirebaseUser1.json", type = FirebaseUser.class),
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void findUserById_givenExistingId_returnCorrectFirebaseUser(FirebaseUser firebaseUser, UserRecord userRecord)
      throws FirebaseAuthException, DataAccessException {
    when(firebaseAuth.getUser(userRecord.getUid())).thenReturn(userRecord);

    var foundUser = firebaseService.findUserById(userRecord.getUid());

    assertThat(foundUser).isEqualTo(firebaseUser);

    verify(firebaseAuth, times(1)).getUser(userRecord.getUid());
  }

  @Test
  void findUserById_givenNonExistingId_throwException() throws FirebaseAuthException {
    var nonExistingId = "nonExistingId";
    doThrow(new FirebaseAuthException(ErrorCode.NOT_FOUND, "user not found", null, null, AuthErrorCode.USER_NOT_FOUND))
        .when(firebaseAuth)
        .getUser(nonExistingId);

    assertThatThrownBy(() -> firebaseService.findUserById(nonExistingId)).isInstanceOf(DataNotFoundException.class);
  }

  @Test
  void findUserById_givenLookupError_throwException() throws FirebaseAuthException {
    var irrelevantId = "irrelevantId";
    doThrow(new FirebaseAuthException(ErrorCode.INTERNAL, "internal firebase error", null, null, AuthErrorCode.EXPIRED_ID_TOKEN))
        .when(firebaseAuth)
        .getUser(irrelevantId);

    assertThatThrownBy(() -> firebaseService.findUserById(irrelevantId)).isInstanceOf(DataAccessException.class);
  }

  //endregion

  //region findUserByEmail

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FirebaseUser1.json", type = FirebaseUser.class),
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void findUserByEmail_givenExistingEmail_returnCorrectFirebaseUser(FirebaseUser firebaseUser, UserRecord userRecord)
      throws FirebaseAuthException, DataAccessException {
    when(firebaseAuth.getUserByEmail(userRecord.getEmail())).thenReturn(userRecord);

    var foundUser = firebaseService.findUserByEmail(userRecord.getEmail());

    assertThat(foundUser).isEqualTo(firebaseUser);

    verify(firebaseAuth, times(1)).getUserByEmail(userRecord.getEmail());
  }

  @Test
  void findUserByEmail_givenNonExistingEmail_throwException() throws FirebaseAuthException {
    var nonExistingEmail = "nonExistingEmail";
    doThrow(new FirebaseAuthException(ErrorCode.NOT_FOUND, "user not found", null, null, AuthErrorCode.USER_NOT_FOUND))
        .when(firebaseAuth)
        .getUserByEmail(nonExistingEmail);

    assertThatThrownBy(() -> firebaseService.findUserByEmail(nonExistingEmail)).isInstanceOf(DataNotFoundException.class);
  }

  @Test
  void findUserByEmail_givenLookupError_throwException() throws FirebaseAuthException {
    var irrelevantEmail = "irrelevantEmail";
    doThrow(new FirebaseAuthException(ErrorCode.INTERNAL, "internal firebase error", null, null, AuthErrorCode.EXPIRED_ID_TOKEN))
        .when(firebaseAuth)
        .getUserByEmail(irrelevantEmail);

    assertThatThrownBy(() -> firebaseService.findUserByEmail(irrelevantEmail)).isInstanceOf(DataAccessException.class);
  }

  //endregion

  //region createUser

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FirebaseUser2.json", type = FirebaseUser.class),
      @JsonFileSource(value = DATA_ROOT + "UserRecord2.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void createUser_validRequest_returnCreatedFirebaseUser(FirebaseUser expectedUser, UserRecord userRecord)
      throws DataCreationException, FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      when(firebaseAuth.createUser(any(UserRecord.CreateRequest.class))).thenReturn(userRecord);

      var createdUser = firebaseService.createUser("email@example.com", "strongPassword");

      assertThat(createdUser).isEqualTo(expectedUser);
    }
  }

  @Test
  void createUser_firebaseError_throwException() throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      doThrow(new FirebaseAuthException(ErrorCode.INTERNAL, "internal error", null, null, AuthErrorCode.EMAIL_ALREADY_EXISTS))
          .when(firebaseAuth)
          .createUser(any(UserRecord.CreateRequest.class));

      assertThatThrownBy(() -> firebaseService.createUser("email@example.com", "strongPassword")).isInstanceOf(DataCreationException.class);
    }
  }

  //endregion

  //region verifyToken

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "FirebaseToken1.json", type = FirebaseToken.class, converter = FirebaseTokenArgumentConverter.class)
  })
  void verifyToken_validToken_returnCredentials(FirebaseToken firebaseToken) throws FirebaseAuthException, AuthenticationException {
    var idToken = "exampleIdToken";
    when(firebaseAuth.verifyIdToken(eq(idToken), anyBoolean())).thenReturn(firebaseToken);

    var credentials = firebaseService.verifyToken(idToken, true);

    assertThat(credentials).isEqualTo(new Credentials(firebaseToken, idToken));

    verify(firebaseAuth, times(1)).verifyIdToken(idToken, true);
  }

  @Test
  void verifyToken_invalidToken_throwException() throws FirebaseAuthException {
    doThrow(new FirebaseAuthException(ErrorCode.INVALID_ARGUMENT, "invalid token", null, null, AuthErrorCode.INVALID_ID_TOKEN))
        .when(firebaseAuth)
        .verifyIdToken(anyString(), anyBoolean());

    assertThatThrownBy(() -> firebaseService.verifyToken("ID-token", false)).isInstanceOf(AuthenticationException.class);
  }

  @Test
  void verifyToken_noToken_returnNull() throws FirebaseAuthException, AuthenticationException {
    doThrow(new IllegalArgumentException())
        .when(firebaseAuth)
        .verifyIdToken(eq(null), anyBoolean());

    var result = firebaseService.verifyToken(null, false);

    assertThat(result).isNull();
  }

  //endregion

  //region revokeRefreshTokens

  @Test
  void revokeRefreshTokens_noInternalError_succeeds() throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      var id = "irrelevantId";

      assertThatCode(() -> firebaseService.revokeRefreshTokens(id)).doesNotThrowAnyException();

      verify(firebaseAuth, times(1)).revokeRefreshTokens(id);
    }
  }

  @Test
  void revokeRefreshTokens_internalError_throwException() throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      doThrow(new FirebaseAuthException(ErrorCode.UNAUTHENTICATED, "invalid token", null, null, AuthErrorCode.INVALID_ID_TOKEN))
          .when(firebaseAuth)
          .revokeRefreshTokens(anyString());

      assertThatThrownBy(() -> firebaseService.revokeRefreshTokens("userId")).isInstanceOf(DataModificationException.class);
    }
  }

  //endregion

  //region addRole

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void addRole_setAdminRoleWithoutInternalError_succeeds(UserRecord userRecord) throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      when(firebaseAuth.getUser(userRecord.getUid())).thenReturn(userRecord);
      when(securityProperties.getValidRoles()).thenReturn(Arrays.stream(Role.values()).map(Role::identifier).collect(Collectors.toList()));

      var addedRole = Role.ADMIN;

      assertThatCode(() -> firebaseService.addRole(userRecord.getUid(), addedRole)).doesNotThrowAnyException();

      verify(firebaseAuth, times(1)).revokeRefreshTokens(userRecord.getUid());
      verify(firebaseAuth, times(1)).setCustomUserClaims(eq(userRecord.getUid()), customClaimMapCaptor.capture());
      assertThat(customClaimMapCaptor.getValue()).containsEntry(addedRole.identifier(), true);
    }
  }

  @Test
  void addRole_setAdminWithNonExistingId_throwException() throws FirebaseAuthException {
    var nonExistingId = "nonExistingId";
    doThrow(new FirebaseAuthException(ErrorCode.NOT_FOUND, "user not found", null, null, AuthErrorCode.USER_NOT_FOUND))
        .when(firebaseAuth)
        .getUser(nonExistingId);

    assertThatThrownBy(() -> firebaseService.addRole(nonExistingId, Role.ADMIN)).isInstanceOf(DataNotFoundException.class);
  }

  @Test
  void addRole_setAdminWithOtherError_throwException() throws FirebaseAuthException {
    doThrow(new FirebaseAuthException(ErrorCode.INTERNAL, "internal error", null, null, AuthErrorCode.CERTIFICATE_FETCH_FAILED))
        .when(firebaseAuth)
        .getUser(anyString());

    assertThatThrownBy(() -> firebaseService.addRole("anyId", Role.ADMIN)).isInstanceOf(DataModificationException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void addRole_setInvalidRole_throwException(UserRecord userRecord) throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      when(firebaseAuth.getUser(userRecord.getUid())).thenReturn(userRecord);

      var addedRole = Role.ADMIN;

      assertThatThrownBy(() -> firebaseService.addRole(userRecord.getUid(), addedRole)).isInstanceOf(DataModificationException.class);
    }
  }


  //endregion

  //region setRole

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void setRole_setAdminRoleWithoutInternalError_succeeds(UserRecord userRecord) throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      when(firebaseAuth.getUser(userRecord.getUid())).thenReturn(userRecord);
      when(securityProperties.getValidRoles()).thenReturn(Arrays.stream(Role.values()).map(Role::identifier).collect(Collectors.toList()));

      var addedRole = Role.ADMIN;

      assertThatCode(() -> firebaseService.setRole(userRecord.getUid(), addedRole)).doesNotThrowAnyException();

      verify(firebaseAuth, times(1)).revokeRefreshTokens(userRecord.getUid());
      verify(firebaseAuth, times(1)).setCustomUserClaims(eq(userRecord.getUid()), customClaimMapCaptor.capture());
      assertThat(customClaimMapCaptor.getValue()).isEqualTo(Map.of(addedRole.identifier(), true));
    }
  }

  @Test
  void setRole_setAdminWithNonExistingId_throwException() throws FirebaseAuthException {
    var nonExistingId = "nonExistingId";
    doThrow(new FirebaseAuthException(ErrorCode.NOT_FOUND, "user not found", null, null, AuthErrorCode.USER_NOT_FOUND))
        .when(firebaseAuth)
        .getUser(nonExistingId);

    assertThatThrownBy(() -> firebaseService.setRole(nonExistingId, Role.ADMIN)).isInstanceOf(DataNotFoundException.class);
  }

  @Test
  void setRole_setAdminWithOtherError_throwException() throws FirebaseAuthException {
    doThrow(new FirebaseAuthException(ErrorCode.INTERNAL, "internal error", null, null, AuthErrorCode.CERTIFICATE_FETCH_FAILED))
        .when(firebaseAuth)
        .getUser(anyString());

    assertThatThrownBy(() -> firebaseService.setRole("anyId", Role.ADMIN)).isInstanceOf(DataModificationException.class);
  }

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void setRole_setInvalidRole_throwException(UserRecord userRecord) throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      when(firebaseAuth.getUser(userRecord.getUid())).thenReturn(userRecord);

      var addedRole = Role.ADMIN;

      assertThatThrownBy(() -> firebaseService.setRole(userRecord.getUid(), addedRole)).isInstanceOf(DataModificationException.class);
    }
  }

  //endregion

  //region removeRole

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void removeRole_noInternalError_succeeds(UserRecord userRecord) throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      when(firebaseAuth.getUser(userRecord.getUid())).thenReturn(userRecord);

      var removedRole = Role.ADMIN;

      assertThatCode(() -> firebaseService.removeRole(userRecord.getUid(), removedRole)).doesNotThrowAnyException();

      verify(firebaseAuth, times(1)).revokeRefreshTokens(userRecord.getUid());
      verify(firebaseAuth, times(1)).setCustomUserClaims(eq(userRecord.getUid()), customClaimMapCaptor.capture());
      assertThat(customClaimMapCaptor.getValue()).doesNotContainKey(removedRole.identifier());
    }
  }

  @Test
  void removeRole_nonExistingId_throwException() throws FirebaseAuthException {
    var nonExistingId = "nonExistingId";
    doThrow(new FirebaseAuthException(ErrorCode.NOT_FOUND, "user not found", null, null, AuthErrorCode.USER_NOT_FOUND))
        .when(firebaseAuth)
        .getUser(nonExistingId);

    assertThatThrownBy(() -> firebaseService.removeRole(nonExistingId, Role.ADMIN)).isInstanceOf(DataNotFoundException.class);
  }

  @Test
  void removeRole_internalError_throwException() throws FirebaseAuthException {
    doThrow(new FirebaseAuthException(ErrorCode.INTERNAL, "internal error", null, null, AuthErrorCode.CERTIFICATE_FETCH_FAILED))
        .when(firebaseAuth)
        .getUser(anyString());

    assertThatThrownBy(() -> firebaseService.removeRole("anyId", Role.ADMIN)).isInstanceOf(DataModificationException.class);
  }

  //endregion

  //region clearRoles

  @ParameterizedTest
  @JsonFileSources(parameters = {
      @JsonFileSource(value = DATA_ROOT + "UserRecord1.json", type = UserRecord.class, converter = UserRecordArgumentConverter.class)
  })
  void clearRoles_noInternalError_succeeds(UserRecord userRecord) throws FirebaseAuthException {
    try (var firebaseAuthStaticMock = mockStatic(FirebaseAuth.class)) {
      firebaseAuthStaticMock.when(FirebaseAuth::getInstance).thenReturn(firebaseAuth);
      when(firebaseAuth.getUser(userRecord.getUid())).thenReturn(userRecord);

      assertThatCode(() -> firebaseService.clearRoles(userRecord.getUid())).doesNotThrowAnyException();

      verify(firebaseAuth, times(1)).revokeRefreshTokens(userRecord.getUid());
      verify(firebaseAuth, times(1)).setCustomUserClaims(eq(userRecord.getUid()), customClaimMapCaptor.capture());
      assertThat(customClaimMapCaptor.getValue()).isEmpty();
    }
  }

  @Test
  void clearRoles_nonExistingId_throwException() throws FirebaseAuthException {
    var nonExistingId = "nonExistingId";
    doThrow(new FirebaseAuthException(ErrorCode.NOT_FOUND, "user not found", null, null, AuthErrorCode.USER_NOT_FOUND))
        .when(firebaseAuth)
        .getUser(nonExistingId);

    assertThatThrownBy(() -> firebaseService.clearRoles(nonExistingId)).isInstanceOf(DataNotFoundException.class);
  }

  @Test
  void clearRoles_internalError_throwException() throws FirebaseAuthException {
    doThrow(new FirebaseAuthException(ErrorCode.INTERNAL, "internal error", null, null, AuthErrorCode.CERTIFICATE_FETCH_FAILED))
        .when(firebaseAuth)
        .getUser(anyString());

    assertThatThrownBy(() -> firebaseService.clearRoles("anyId")).isInstanceOf(DataModificationException.class);
  }

  //endregion
}
