package com.witness.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.witness.server.enumeration.Role;
import com.witness.server.exception.AuthenticationException;
import com.witness.server.model.Credentials;
import com.witness.server.model.FirebaseUser;
import com.witness.server.service.SecurityService;
import com.witness.server.service.TimeService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SecurityServiceImpl implements SecurityService {
  private static final String BEARER_PREFIX = "Bearer ";
  private final TimeService timeService;
  private final ObjectMapper objectMapper;

  @Autowired
  public SecurityServiceImpl(TimeService timeService, ObjectMapper objectMapper) {
    this.timeService = timeService;
    this.objectMapper = objectMapper;
  }

  @Override
  public FirebaseUser getCurrentUser() {
    FirebaseUser firebaseUserPrincipal = null;

    var securityContext = SecurityContextHolder.getContext();
    var principal = securityContext.getAuthentication().getPrincipal();
    if (principal instanceof FirebaseUser) {
      firebaseUserPrincipal = ((FirebaseUser) principal);
    }

    return firebaseUserPrincipal;
  }

  @Override
  public Credentials getCurrentCredentials() {
    var securityContext = SecurityContextHolder.getContext();
    return (Credentials) securityContext.getAuthentication().getCredentials();
  }

  @Override
  public String getBearerToken(HttpServletRequest request) {
    String bearerToken = null;

    var authorization = request.getHeader("Authorization");
    if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
      bearerToken = authorization.substring(BEARER_PREFIX.length());
    }

    return bearerToken;
  }

  @Override
  public Optional<List<Role>> extractRoles(Authentication authentication) {
    if (authentication == null || authentication.getAuthorities() == null) {
      return Optional.empty();
    }

    return Optional.of(
        authentication.getAuthorities().stream()
            .map(a -> Role.fromIdentifier(a.getAuthority()))
            .filter(Objects::nonNull)
            .collect(Collectors.toList()));
  }

  @Override
  public void replyAuthenticationError(HttpServletResponse response, AuthenticationException exception) throws IOException {
    var errorObject = new HashMap<String, Object>();
    var errorStatus = HttpStatus.UNAUTHORIZED;

    errorObject.put("message", exception.getMessage());
    errorObject.put("error", errorStatus);
    errorObject.put("status", errorStatus.value());
    errorObject.put("errorKey", exception.getErrorKey());
    errorObject.put("timestamp", timeService.getCurrentTime());

    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(errorStatus.value());
    response.getWriter().write(objectMapper.writeValueAsString(errorObject));
  }
}
