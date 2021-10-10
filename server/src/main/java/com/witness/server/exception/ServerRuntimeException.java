package com.witness.server.exception;

import com.witness.server.enumeration.ServerError;

/**
 * An unchecked base exception to denote errors in the server application.
 */
public abstract class ServerRuntimeException extends RuntimeException {
  protected final ServerError errorKey;

  public ServerRuntimeException() {
    errorKey = ServerError.UNDEFINED_ERROR;
  }

  public ServerRuntimeException(String message) {
    super(message);
    errorKey = ServerError.UNDEFINED_ERROR;
  }

  public ServerRuntimeException(String message, ServerError errorKey) {
    super(message);
    this.errorKey = errorKey;
  }

  public ServerRuntimeException(String message, Throwable cause) {
    super(message, cause);
    errorKey = ServerError.UNDEFINED_ERROR;
  }

  public ServerRuntimeException(String message, ServerError errorKey, Throwable cause) {
    super(message, cause);
    this.errorKey = errorKey;
  }

  public ServerRuntimeException(Throwable cause) {
    super(cause);
    errorKey = ServerError.UNDEFINED_ERROR;
  }

  public ServerRuntimeException(Throwable cause, ServerError errorKey) {
    super(cause);
    this.errorKey = errorKey;
  }

  public ServerError getErrorKey() {
    return errorKey;
  }
}
