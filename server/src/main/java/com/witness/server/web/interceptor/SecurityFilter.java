package com.witness.server.web.interceptor;

import com.witness.server.configuration.SecurityProperties;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.mapper.FirebaseMapper;
import com.witness.server.model.Credentials;
import com.witness.server.service.FirebaseService;
import com.witness.server.service.SecurityService;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A custom {@link OncePerRequestFilter} that is registered before Spring's {@link UsernamePasswordAuthenticationFilter}. It serves the purpose of
 * verifying the validity of a Bearer JWT (sent in the Authorization header of an HTTP request). This JWT represents a Firebase ID token that is then
 * sent to the Firebase authentication server, which in turn replies with a response indicating the validity. Based on this response, the incoming
 * request is processed further or rejected with an appropriate HTTP status code.
 */
@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {
  private final SecurityProperties securityProperties;
  private final FirebaseService firebaseService;
  private final SecurityService securityService;
  private final FirebaseMapper firebaseMapper;

  @Autowired
  public SecurityFilter(SecurityProperties securityProperties, FirebaseService firebaseService, SecurityService securityService,
                        FirebaseMapper firebaseMapper) {
    this.securityProperties = securityProperties;
    this.firebaseService = firebaseService;
    this.securityService = securityService;
    this.firebaseMapper = firebaseMapper;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      authorize(request);
      filterChain.doFilter(request, response);
    } catch (AuthenticationException e) {
      securityService.replyAuthenticationError(response, e);
    }
  }

  private void authorize(HttpServletRequest request) throws AuthenticationException {
    var bearerJwt = securityService.getBearerToken(request);
    var firebaseCredentials = firebaseService.verifyToken(bearerJwt, securityProperties.isCheckTokenRevoked());

    if (firebaseCredentials == null) {
      // No authentication was provided. If request was not sent to a public ("allowed") path, a 401 response will be sent by the unauthorizedHandler
      // (see CORS config). Otherwise, the request will be processed, but there is no point in handling roles. Hence, in both cases, return.
      return;
    }

    processRoles(request, firebaseCredentials);
  }

  private void processRoles(HttpServletRequest request, Credentials firebaseCredentials) {
    var authorities = new ArrayList<GrantedAuthority>();
    var token = firebaseCredentials.getDecodedToken();
    var firebaseUser = firebaseMapper.tokenToUser(token);

    token.getClaims().forEach((claim, value) -> authorities.add(new SimpleGrantedAuthority(claim)));

    // Set security context with roles from JWT claims to support Spring's @PreAuthorization annotation on methods.
    var authentication = new UsernamePasswordAuthenticationToken(firebaseUser, firebaseCredentials, authorities);
    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
