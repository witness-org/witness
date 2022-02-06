package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * An exception for when the creation of a data record fails.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataCreationException extends DataAccessException {
  public DataCreationException() {
    super();
  }

  public DataCreationException(String message, ServerError errorKey) {
    super(message, errorKey);
  }

  public DataCreationException(String message, ServerError errorKey, Throwable cause) {
    super(message, errorKey, cause);
  }

  public DataCreationException(Throwable cause, ServerError errorKey) {
    super(cause, errorKey);
  }
}
