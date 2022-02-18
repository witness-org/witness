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
import com.witness.server.entity.user.User;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.exception.DataAccessException;
import com.witness.server.integration.BaseIntegrationTest;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;
import com.witness.server.repository.UserRepository;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.SecurityService;
import com.witness.server.service.UserService;
import com.witness.server.util.isolation.DatabaseResetService;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.assertj.core.util.TriFunction;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Answers;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

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

  @Autowired
  @Qualifier("H2")
  private DatabaseResetService databaseResetService;

  @BeforeEach
  void beforeEach() {
    databaseResetService.resetDatabase();
  }

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

  protected <T> ResponseEntity<T> get(TestAuthentication authMode, String url, MultiValueMap<String, String> queryParams,
                                      ParameterizedTypeReference<T> responseType) {
    return exchange(authMode, url, HttpMethod.GET, queryParams, null, responseType);
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

  protected <T, U> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, MultiValueMap<String, String> queryParams,
                                              U requestBody, Class<T> responseType) {
    return exchange(authMode, url, method, queryParams, requestBody,
        (requestUri, httpMethod, requestEntity) -> restTemplate.exchange(requestUri, httpMethod, requestEntity, responseType));
  }

  protected <T, U> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, MultiValueMap<String, String> queryParams,
                                              U requestBody, ParameterizedTypeReference<T> responseType) {
    return exchange(authMode, url, method, queryParams, requestBody,
        (requestUri, httpMethod, requestEntity) -> restTemplate.exchange(requestUri, httpMethod, requestEntity, responseType));
  }

  @SneakyThrows({AuthenticationException.class, IOException.class})
  private <T, U> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method,
                                            MultiValueMap<String, String> queryParams, U requestBody,
                                            TriFunction<URI, HttpMethod, HttpEntity<U>, ResponseEntity<T>> exchangeFunction) {
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

    // Content Types for OpenAPI specifications (source: https://github.com/OAI/OpenAPI-Specification/issues/110#issuecomment-364498200)
    //   - "application/vnd.oai.openapi" (YAML variant), not yet registered with IANA
    //   - "application/vnd.oai.openapi+json" (JSON only variant), not yet registered with IANA
    var headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON,
        new MediaType("application", "vnd.oai.openapi", StandardCharsets.UTF_8),
        new MediaType("application", "vnd.oai.openapi+json", StandardCharsets.UTF_8)));

    var requestUri = UriComponentsBuilder
        .fromHttpUrl(url)
        .encode(StandardCharsets.UTF_8)
        .queryParams(encodeQueryParameters(queryParams))
        .build(true)
        .toUri();
    var requestEntity = new HttpEntity<>(requestBody, headers);

    return exchangeFunction.apply(requestUri, method, requestEntity);
  }

  protected static <K, V> MultiValueMap<K, V> toMultiValueMap(Map<K, V> map) {
    var multiValueMap = new LinkedMultiValueMap<K, V>();
    map.forEach((key, value) -> multiValueMap.put(key, Collections.singletonList(value)));
    return multiValueMap;
  }

  protected void persistUsers(User... users) {
    persistEntities(userRepository, users);
  }

  @SneakyThrows(DataAccessException.class)
  protected void persistUserAndMockLoggedIn(User user) {
    var firebaseUser = getFirebaseUser(user);

    persistUsers(user);
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

  private static FirebaseUser getFirebaseUser(User user) {
    return FirebaseUser.builder()
        .name(user.getUsername())
        .email(user.getEmail())
        .uid(user.getFirebaseId())
        .isEmailVerified(true)
        .issuer("TestUserIssuer")
        .picture("TestUserPicture")
        .build();
  }

  private MultiValueMap<String, String> encodeQueryParameters(MultiValueMap<String, String> queryParams) {
    if (queryParams == null) {
      return null;
    }

    var encodedQueryParams = new LinkedMultiValueMap<String, String>();
    queryParams.forEach((key, value) -> encodedQueryParams.put(key, value.stream()
        .map(item -> UriUtils.encode(item, StandardCharsets.UTF_8))
        .collect(Collectors.toList())));
    return encodedQueryParams;
  }
}
