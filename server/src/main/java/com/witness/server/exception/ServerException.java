package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;

/**
 * A base exception to denote errors in the server application.
 */
public abstract class ServerException extends Exception {
  protected final ServerError errorKey;

  public ServerException() {
    errorKey = ServerError.UNDEFINED_ERROR;
  }

  public ServerException(String message, ServerError errorKey) {
    super(message);
    this.errorKey = errorKey;
  }

  public ServerException(String message, ServerError errorKey, Throwable cause) {
    super(message, cause);
    this.errorKey = errorKey;
  }

  public ServerException(Throwable cause, ServerError errorKey) {
    super(cause);
    this.errorKey = errorKey;
  }

  public ServerError getErrorKey() {
    return errorKey;
  }
}
