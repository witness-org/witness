package com.witness.server.integration.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.witness.server.entity.User;
import com.witness.server.enumeration.Role;
import com.witness.server.enumeration.Sex;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.exception.DataAccessException;
import com.witness.server.integration.BaseIntegrationTest;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;
import com.witness.server.repository.UserRepository;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.SecurityService;
import com.witness.server.service.UserService;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.mockito.Answers;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // TODO proper reset of database (see GitLab issue #51)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerIntegrationTest extends BaseIntegrationTest {
  @LocalServerPort
  private int port;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @SuppressWarnings("unused") // mocks external library, needed as bean for application context to load
  @MockBean
  private FirebaseAuth firebaseAuth;

  @SuppressWarnings("unused") // mocks external library, needed as bean for application context to load
  @MockBean
  private FirebaseApp firebaseApp;

  @MockBean
  protected FirebaseService firebaseService;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean(answer = Answers.CALLS_REAL_METHODS)
  private SecurityService securityService;

  @Autowired
  private TestRestTemplate restTemplate;

  abstract String getEndpointUrl();

  protected String requestUrl() {
    return "http://localhost:" + port + "/" + getEndpointUrl();
  }

  protected String requestUrl(String suffix, Object... args) {
    return requestUrl() + "/" + String.format(suffix, args);
  }


  protected <T> ResponseEntity<T> get(TestAuthentication authMode, String url, Class<T> clazz) {
    return exchange(authMode, url, HttpMethod.GET, clazz);
  }

  protected <T> ResponseEntity<T> get(TestAuthentication authMode, String url, MultiValueMap<String, String> queryParams, Class<T> clazz) {
    return exchange(authMode, url, HttpMethod.GET, queryParams, clazz);
  }

  protected <T> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, Class<T> responseType) {
    return exchange(authMode, url, method, null, null, responseType);
  }

  protected <T> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method,
                                           MultiValueMap<String, String> queryParams, Class<T> responseType) {
    return exchange(authMode, url, method, queryParams, null, responseType);
  }

  protected <T, U> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, U requestBody,
                                              Class<T> responseType) {
    return exchange(authMode, url, method, null, requestBody, responseType);
  }

  @SneakyThrows({AuthenticationException.class, IOException.class})
  protected <T, U> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method,
                                              MultiValueMap<String, String> queryParams, U requestBody, Class<T> responseType) {
    doAnswer(this::stubAuthenticationError)
        .when(securityService)
        .replyAuthenticationError(any(HttpServletResponse.class), any(AuthenticationException.class));

    if (authMode == TestAuthentication.NONE) {
      when(firebaseService.verifyToken(nullable(String.class), anyBoolean())).thenReturn(null);
    } else {
      var token = mock(FirebaseToken.class);
      when(token.getClaims()).thenReturn(getClaimsForAuthMode(authMode));
      when(securityService.extractRoles(any(Authentication.class))).thenReturn(getRolesForAuthMode(authMode));
      when(firebaseService.verifyToken(nullable(String.class), anyBoolean())).thenReturn(new Credentials(token, "integrationTestToken"));
    }

    var headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

    var uriBuilder = UriComponentsBuilder
        .fromHttpUrl(url)
        .queryParams(queryParams);
    var requestEntity = new HttpEntity<>(requestBody, headers);

    return restTemplate.exchange(uriBuilder.toUriString(), method, requestEntity, responseType);
  }

  protected static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, V> map) {
    var multiValueMap = new LinkedMultiValueMap<K, V>();
    map.forEach((key, value) -> multiValueMap.put(key, Collections.singletonList(value)));
    return multiValueMap;
  }

  @SneakyThrows(DataAccessException.class)
  protected void persistUserAndMockLoggedIn(TestAuthentication authMode) {
    var firebaseUser = getDummyFirebaseUser();
    var user = getDummyUser(firebaseUser, getRoleFromAuthenticationMode(authMode));

    userRepository.save(user);
    when(securityService.getCurrentUser()).thenReturn(firebaseUser);
    when(firebaseService.findUserById(firebaseUser.getUid())).thenReturn(firebaseUser);
  }

  protected <T, U> void persistEntities(JpaRepository<T, U> repository, Iterable<? extends T> entities) {
    repository.saveAllAndFlush(entities);
  }

  @SafeVarargs
  protected final <T, U> void persistEntities(JpaRepository<T, U> repository, T... entities) {
    persistEntities(repository, Arrays.stream(entities).collect(Collectors.toList()));
  }

  @SneakyThrows(DataAccessException.class)
  protected User getLoggedInUser() {
    var currentUser = securityService.getCurrentUser();
    return userService.findByFirebaseId(currentUser.getUid());
  }

  private Object stubAuthenticationError(InvocationOnMock invocation) throws IOException {
    var errorObject = new HashMap<String, Object>();
    var errorStatus = HttpStatus.UNAUTHORIZED;

    var exception = invocation.getArgument(1, AuthenticationException.class);

    errorObject.put("message", exception.getMessage());
    errorObject.put("error", errorStatus);
    errorObject.put("status", errorStatus.value());
    errorObject.put("errorKey", exception.getErrorKey());
    errorObject.put("timestamp", Instant.now().atZone(ZoneId.of("UTC")));

    var response = invocation.getArgument(0, HttpServletResponse.class);

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(errorStatus.value());
    response.getWriter().write(objectMapper.writeValueAsString(errorObject));
    return null;
  }

  private Map<String, Object> getClaimsForAuthMode(TestAuthentication authMode) {
    if (authMode == TestAuthentication.REGULAR) {
      return Collections.emptyMap();
    }

    var roleClaim = switch (authMode) {
      case ADMIN -> Role.ADMIN.identifier();
      case PREMIUM -> Role.PREMIUM.identifier();
      default -> throw new IllegalArgumentException("Mode " + authMode + " ought not be handled by this method.");
    };

    return Map.of(roleClaim, true);
  }

  private Optional<List<Role>> getRolesForAuthMode(TestAuthentication authMode) {
    return switch (authMode) {
      case ADMIN -> Optional.of(Collections.singletonList(Role.ADMIN));
      case PREMIUM -> Optional.of(Collections.singletonList(Role.PREMIUM));
      case REGULAR -> Optional.of(Collections.emptyList());
      case NONE -> Optional.empty();
    };
  }

  private static FirebaseUser getDummyFirebaseUser() {
    return FirebaseUser.builder()
        .name("TestUser")
        .email("user@test.com")
        .uid("TestUserFirebaseId")
        .isEmailVerified(true)
        .issuer("TestUserIssuer")
        .picture("TestUserPicture")
        .build();
  }

  private static User getDummyUser(FirebaseUser firebaseUser, Role role) {
    var currentTime = ZonedDateTime.now();

    return User.builder()
        .username(firebaseUser.getName())
        .email(firebaseUser.getEmail())
        .firebaseId(firebaseUser.getUid())
        .createdAt(currentTime)
        .modifiedAt(currentTime)
        .sex(Sex.FEMALE)
        .height(170L)
        .role(role)
        .build();
  }

  private static Role getRoleFromAuthenticationMode(TestAuthentication authMode) {
    return switch (authMode) {
      case ADMIN -> Role.ADMIN;
      case PREMIUM -> Role.PREMIUM;
      case REGULAR, NONE -> null;
    };
  }
}
