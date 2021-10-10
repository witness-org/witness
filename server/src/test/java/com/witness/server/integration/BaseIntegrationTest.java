package com.witness.server.integration;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.model.Credentials;
import com.witness.server.service.FirebaseService;
import java.util.Collections;
import java.util.Map;
import lombok.SneakyThrows;
import org.mockito.Answers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
public abstract class BaseIntegrationTest {
  @LocalServerPort
  private int port;

  @MockBean(answer = Answers.CALLS_REAL_METHODS)
  protected FirebaseAuth firebaseAuth;

  @MockBean
  protected FirebaseApp firebaseApp;

  @MockBean
  protected FirebaseService firebaseService;

  @Autowired
  protected TestRestTemplate restTemplate;

  abstract String getEndpointUrl();

  protected String requestUrl() {
    return "http://localhost:" + port + "/" + getEndpointUrl();
  }

  protected String requestUrl(String suffix, Object... args) {
    return requestUrl() + "/" + String.format(suffix, args);
  }

  protected <T> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, Class<T> responseType) {
    return exchange(authMode, url, method, null, null, responseType);
  }

  protected <T> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, MultiValueMap<String, String> queryParams,
                                           Class<T> responseType) {
    return exchange(authMode, url, method, queryParams, null, responseType);
  }

  protected <T, U> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, U requestBody, Class<T> responseType) {
    return exchange(authMode, url, method, null, requestBody, responseType);
  }

  @SneakyThrows(AuthenticationException.class)
  protected <T, U> ResponseEntity<T> exchange(TestAuthentication authMode, String url, HttpMethod method, MultiValueMap<String, String> queryParams,
                                              U requestBody, Class<T> responseType) {
    if (authMode == TestAuthentication.NONE) {
      when(firebaseService.verifyToken(nullable(String.class), anyBoolean())).thenReturn(null);
    } else {
      var token = Mockito.mock(FirebaseToken.class);
      when(token.getClaims()).thenReturn(getClaimsForAuthMode(authMode));
      when(firebaseService.verifyToken(nullable(String.class), anyBoolean())).thenReturn(new Credentials(token, "testToken"));
    }

    var headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

    var uriBuilder = UriComponentsBuilder
        .fromHttpUrl(url)
        .queryParams(queryParams);
    var requestEntity = new HttpEntity<>(requestBody, headers);

    return restTemplate.exchange(uriBuilder.toUriString(), method, requestEntity, responseType);
  }

  private Map<String, Object> getClaimsForAuthMode(TestAuthentication authMode) {
    String roleClaim;
    switch (authMode) {
      case ADMIN:
        roleClaim = Role.ADMIN.identifier();
        break;
      case PREMIUM:
        roleClaim = Role.PREMIUM.identifier();
        break;
      case REGULAR:
        return Collections.emptyMap();
      default:
        throw new IllegalArgumentException("Mode " + authMode + " ought not be handled by this method.");
    }
    return Map.of(roleClaim, true);
  }
}
