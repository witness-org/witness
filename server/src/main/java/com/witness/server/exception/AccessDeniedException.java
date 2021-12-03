package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for when a user does not have the rights required to perform an operation.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends ServerException {
  public AccessDeniedException() {
  }

  public AccessDeniedException(String message) {
    super(message);
  }

  public AccessDeniedException(String message, ServerError errorKey) {
    super(message, errorKey);
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(message, cause);
  }

  public AccessDeniedException(String message, ServerError errorKey, Throwable cause) {
    super(message, errorKey, cause);
  }

  public AccessDeniedException(Throwable cause) {
    super(cause);
  }

  public AccessDeniedException(Throwable cause, ServerError errorKey) {
    super(cause, errorKey);
  }
}
