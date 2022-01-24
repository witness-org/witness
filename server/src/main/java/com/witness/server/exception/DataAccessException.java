package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A general exception for errors that are related to failed data accesses.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataAccessException extends ServerException {
  public DataAccessException() {
  }

  public DataAccessException(String message, ServerError errorKey) {
    super(message, errorKey);
  }

  public DataAccessException(String message, ServerError errorKey, Throwable cause) {
    super(message, errorKey, cause);
  }

  public DataAccessException(Throwable cause, ServerError errorKey) {
    super(cause, errorKey);
  }
}
