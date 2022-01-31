package com.witness.server.exception;


import com.witness.server.enumeration.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for when a request does not fulfill the security requirements.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends ServerException {
  public AuthenticationException() {
    super();
  }

  public AuthenticationException(String message, ServerError errorKey) {
    super(message, errorKey);
  }

  public AuthenticationException(String message, ServerError errorKey, Throwable cause) {
    super(message, errorKey, cause);
  }

  public AuthenticationException(Throwable cause, ServerError errorKey) {
    super(cause, errorKey);
  }
}
