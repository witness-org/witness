package com.witness.server.service;

import com.witness.server.enumeration.Role;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Provides methods related to security, in the sense of Spring Security aspects and HTTP handling.
 */
public interface SecurityService {
  /**
   * Determines the user which is authenticated for the request currently being handled.
   *
   * @return the currently authenticated user, represented by a {@link FirebaseUser} instance
   */
  FirebaseUser getCurrentUser();

  /**
   * Determines the credentials of the user which is authenticated for the request currently being handled. This entails the original JWT (ID Token)
   * as well as the decoded Firebase representation of this token.
   *
   * @return the credentials of the currently authenticated user, represented by a {@link Credentials} instance
   */
  Credentials getCurrentCredentials();

  /**
   * Extracts the bearer token, which holds the JWT/ID token, from the "Authorization" header of an HTTP request.
   *
   * @param request the request from which a bearer token should be extracted
   * @return the encoded token used to authenticate {@code request}
   */
  String getBearerToken(HttpServletRequest request);

  /**
   * Determines the roles of from the granted authorities of a {@link Authentication} object.
   *
   * @param authentication the {@link Authentication} object of the {@link SecurityContext} of the current request. May be {@code null}.
   * @return an optional list of {@link Role}s that represents the roles of the user who is authenticated for the current request. A value is present
   *     only if authentication was provided (i.e. a bearer token was present in the {@code Authorization} header of the request). More specifically,
   *     if no authentication was provided (e.g. on a public operation), the {@link Optional} does not have a value. If the current user is a regular
   *     user, i.e. does not have a dedicated role, the {@link Optional}'s value is an empty list. If the current user does have at least one
   *     dedicated role assigned, the {@link Optional}'s value represents a {@link List} containing these roles.
   * @see SecurityContextHolder
   */
  Optional<List<Role>> extractRoles(Authentication authentication);

  /**
   * Writes a JSON-serialized error response with status code {@link HttpStatus#UNAUTHORIZED} as HTTP response based on an
   * {@link AuthenticationException} object containing information on the error cause.
   *
   * @param response the servlet response whose writer is used to send the response
   * @param error    provides information about the type of authentication error to be returned
   * @throws IOException if an input or output exception occurred, e.g. during JSON serialization
   */
  void replyAuthenticationError(HttpServletResponse response, AuthenticationException error) throws IOException;
}
