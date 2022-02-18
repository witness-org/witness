package com.witness.server.enumeration;

import com.google.firebase.auth.AuthErrorCode;
import java.util.Arrays;

/**
 * Represents an error returned by the REST API of this application. This error may be interpreted and mapped to localized error messages by clients.
 */
public enum ServerError {

  /**
   * Failed to retrieve public key certificates required to verify JWTs.
   */
  CERTIFICATE_FETCH_FAILED,

  /**
   * No IdP configuration found for the given identifier.
   */
  CONFIGURATION_NOT_FOUND,

  /**
   * A user already exists with the provided email.
   */
  EMAIL_ALREADY_EXISTS,

  /**
   * No user record found for the given email, typically raised when generating a password reset link using an email for a user that
   * is not already registered.
   */
  EMAIL_NOT_FOUND,

  /**
   * The specified ID token is expired.
   */
  EXPIRED_ID_TOKEN,

  /**
   * The specified session cookie is expired.
   */
  EXPIRED_SESSION_COOKIE,

  /**
   * The provided dynamic link domain is not configured or authorized for the current project.
   */
  INVALID_DYNAMIC_LINK_DOMAIN,

  /**
   * The specified ID token is invalid.
   */
  INVALID_ID_TOKEN,

  /**
   * The specified session cookie is invalid.
   */
  INVALID_SESSION_COOKIE,

  /**
   * A user already exists with the provided phone number.
   */
  PHONE_NUMBER_ALREADY_EXISTS,

  /**
   * The specified ID token has been revoked.
   */
  REVOKED_ID_TOKEN,

  /**
   * The specified session cookie has been revoked.
   */
  REVOKED_SESSION_COOKIE,

  /**
   * Tenant ID in the JWT does not match.
   */
  TENANT_ID_MISMATCH,

  /**
   * No tenant found for the given identifier.
   */
  TENANT_NOT_FOUND,

  /**
   * A user already exists with the provided UID.
   */
  UID_ALREADY_EXISTS,

  /**
   * The domain of the continue URL is not whitelisted. Whitelist the domain in the Firebase console.
   */
  UNAUTHORIZED_CONTINUE_URL,

  /**
   * No user record found for the given identifier.
   */
  USER_NOT_FOUND,

  /**
   * The user record is disabled.
   */
  USER_DISABLED,

  /**
   * The persisted Firebase ID of a user does not point to an actually existing user at the Firebase Auth server or the email addresses do not match.
   */
  USER_INCONSISTENCY,

  /**
   * Request not authorized or another authorization-related operation failed, e.g. due to missing token.
   */
  AUTHORIZATION_NOT_GRANTED,

  /**
   * Request was valid and operation exists, but user does not have required rights.
   */
  INSUFFICIENT_PRIVILEGES,

  /**
   * New user entry could not be created for undefined reasons.
   */
  COULD_NOT_CREATE_USER,

  /**
   * Request was not handled because the request parameters were invalid.
   */
  VALIDATION_ERROR,

  /**
   * The requested role is not valid in the application context.
   */
  INVALID_ROLE,

  /**
   * An undefined error during the lookup of a data record occurred.
   */
  LOOKUP_FAILURE,

  /**
   * No exercise record found for the given identifier.
   */
  EXERCISE_NOT_FOUND,

  /**
   * There already exists an initial exercise with the given name.
   */
  INITIAL_EXERCISE_EXISTS,

  /**
   * There already exists a user exercise for the given user with the given name.
   */
  USER_EXERCISE_EXISTS,

  /**
   * The user exercise with the given identifier was not created by the requesting user.
   */
  USER_EXERCISE_NOT_CREATED_BY_USER,

  /**
   * The position of a set log must not be changed with the current request.
   */
  SET_LOG_POSITION_CHANGE_FORBIDDEN,

  /**
   * No workout log record found for the given identifier.
   */
  WORKOUT_LOG_NOT_FOUND,

  /**
   * No exercise log record found for the given identifier.
   */
  EXERCISE_LOG_NOT_FOUND,

  /**
   * No set log record found for the given identifier.
   */
  SET_LOG_NOT_FOUND,

  /**
   * Request cannot be processed because given workout log was created by another user.
   */
  WORKOUT_LOG_NOT_BY_USER,

  /**
   * The specified exercise log is not part of the given workout log.
   */
  EXERCISE_LOG_NOT_IN_WORKOUT_LOG,

  /**
   * The specified set log is not part of the given exercise log.
   */
  SET_LOG_NOT_IN_EXERCISE_LOG,

  /**
   * Logging type is not applicable to given exercise.
   */
  INVALID_LOGGING_TYPE,

  /**
   * The new assignment of positions to log entries contains too few or too many entries.
   */
  POSITION_MAP_INVALID,

  /**
   * The new assignment of positions to log entries contains duplicates.
   */
  POSITION_MAP_NOT_UNIQUE,


  /**
   * The start date of a workout logging period lies after its end date.
   */
  WORKOUT_LOGGING_START_DATE_AFTER_END_DATE,

  /**
   * Not further defined error.
   */
  UNDEFINED_ERROR;

  /**
   * Converts a {@link AuthErrorCode} returned by Firebase to a managed {@link ServerError}.
   *
   * @param authErrorCode the authentication error code generated by Firebase
   * @return a {@link ServerError} that is equivalent to {@code authErrorCode}. If no matching {@link ServerError} is found, the generic
   *     {@link ServerError#AUTHORIZATION_NOT_GRANTED} is returned.
   */
  public static ServerError fromFirebaseError(AuthErrorCode authErrorCode) {
    return fromFirebaseError(authErrorCode, AUTHORIZATION_NOT_GRANTED);
  }


  /**
   * Converts a {@link AuthErrorCode} returned by Firebase to a managed {@link ServerError}.
   *
   * @param authErrorCode the authentication error code generated by Firebase
   * @param fallback      a {@link ServerError} to be returned if no {@link ServerError} matching {@code authErrorCode} exactly exists
   * @return a {@link ServerError} that is equivalent to {@code authErrorCode}. If no matching {@link ServerError} is found, {@code fallback} is
   *     returned.
   */
  public static ServerError fromFirebaseError(AuthErrorCode authErrorCode, ServerError fallback) {
    if (authErrorCode == null) {
      return fallback;
    }

    return Arrays.stream(ServerError.values())
        .filter(x -> x.name().equals(authErrorCode.name()))
        .findFirst()
        .orElse(fallback);
  }
}
