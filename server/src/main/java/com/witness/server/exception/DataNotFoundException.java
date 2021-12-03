package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for when a requested data record is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataNotFoundException extends DataAccessException {
  public DataNotFoundException() {
  }

  public DataNotFoundException(String message) {
    super(message);
  }

  public DataNotFoundException(String message, ServerError errorKey) {
    super(message, errorKey);
  }

  public DataNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataNotFoundException(String message, ServerError errorKey, Throwable cause) {
    super(message, errorKey, cause);
  }

  public DataNotFoundException(Throwable cause) {
    super(cause);
  }

  public DataNotFoundException(Throwable cause, ServerError errorKey) {
    super(cause, errorKey);
  }
}
