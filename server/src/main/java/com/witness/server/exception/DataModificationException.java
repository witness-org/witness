package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for when the modification of a data record fails.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataModificationException extends DataAccessException {
  public DataModificationException() {
  }

  public DataModificationException(String message) {
    super(message);
  }

  public DataModificationException(String message, ServerError errorKey) {
    super(message, errorKey);
  }

  public DataModificationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DataModificationException(String message, ServerError errorKey, Throwable cause) {
    super(message, errorKey, cause);
  }

  public DataModificationException(Throwable cause) {
    super(cause);
  }

  public DataModificationException(Throwable cause, ServerError errorKey) {
    super(cause, errorKey);
  }
}
