package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for when a request contains data that cannot be persisted because they violate one or multiple constraints.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends ServerException {
  public InvalidRequestException() {
  }

  public InvalidRequestException(String message) {
    super(message);
  }

  public InvalidRequestException(String message, ServerError errorKey) {
    super(message, errorKey);
  }

  public InvalidRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidRequestException(String message, ServerError errorKey, Throwable cause) {
    super(message, errorKey, cause);
  }

  public InvalidRequestException(Throwable cause) {
    super(cause);
  }

  public InvalidRequestException(Throwable cause, ServerError errorKey) {
    super(cause, errorKey);
  }
}
