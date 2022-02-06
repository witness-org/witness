import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/common/string_localizer.dart';

final _logger = getLogger('error_key_translator');

/// Represents a mapping from error keys which should be resolved to a specific localized string.
/// Since [_errorKeys] cannot be `const` due to its dependency on `StringLocalizations`, we cannot put it _into_ the mixin declaration because then
/// consumer widgets could not be `const` anymore. Therefore, it is a file-level variable.
final _errorKeys = <String, String Function(StringLocalizations uiStrings)>{
  'CERTIFICATE_FETCH_FAILED': (final uiStrings) => uiStrings.errorTranslation_certificateFetchFailed,
  'CONFIGURATION_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_configurationNotFound,
  'EMAIL_ALREADY_EXISTS': (final uiStrings) => uiStrings.errorTranslation_emailAlreadyExists,
  'EMAIL_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_emailNotFound,
  'EXPIRED_ID_TOKEN': (final uiStrings) => uiStrings.errorTranslation_expiredIdToken,
  'EXPIRED_SESSION_COOKIE': (final uiStrings) => uiStrings.errorTranslation_expiredSessionCookie,
  'INVALID_DYNAMIC_LINK_DOMAIN': (final uiStrings) => uiStrings.errorTranslation_invalidDynamicLinkDomain,
  'INVALID_ID_TOKEN': (final uiStrings) => uiStrings.errorTranslation_invalidIdToken,
  'INVALID_SESSION_COOKIE': (final uiStrings) => uiStrings.errorTranslation_invalidSessionCookie,
  'PHONE_NUMBER_ALREADY_EXISTS': (final uiStrings) => uiStrings.errorTranslation_phoneNumberAlreadyExists,
  'REVOKED_ID_TOKEN': (final uiStrings) => uiStrings.errorTranslation_revokedIdToken,
  'REVOKED_SESSION_COOKIE': (final uiStrings) => uiStrings.errorTranslation_revokedSessionCookie,
  'TENANT_ID_MISMATCH': (final uiStrings) => uiStrings.errorTranslation_tenantIdMismatch,
  'TENANT_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_tenantNotFound,
  'UID_ALREADY_EXISTS': (final uiStrings) => uiStrings.errorTranslation_uidAlreadyExists,
  'UNAUTHORIZED_CONTINUE_URL': (final uiStrings) => uiStrings.errorTranslation_unauthorizedContinueUrl,
  'USER_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_userNotFound,
  'USER_DISABLED': (final uiStrings) => uiStrings.errorTranslation_userDisabled,
  'USER_INCONSISTENCY': (final uiStrings) => uiStrings.errorTranslation_userInconsistency,
  'AUTHORIZATION_NOT_GRANTED': (final uiStrings) => uiStrings.errorTranslation_authorizationNotGranted,
  'INSUFFICIENT_PRIVILEGES': (final uiStrings) => uiStrings.errorTranslation_insufficientPrivileges,
  'COULD_NOT_CREATE_USER': (final uiStrings) => uiStrings.errorTranslation_couldNotCreateUser,
  'VALIDATION_ERROR': (final uiStrings) => uiStrings.errorTranslation_validationError,
  'INVALID_ROLE': (final uiStrings) => uiStrings.errorTranslation_invalidRole,
  'LOOKUP_FAILURE': (final uiStrings) => uiStrings.errorTranslation_lookupFailure,
  'EXERCISE_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_exerciseNotFound,
  'INITIAL_EXERCISE_EXISTS': (final uiStrings) => uiStrings.errorTranslation_initialExerciseExists,
  'USER_EXERCISE_EXISTS': (final uiStrings) => uiStrings.errorTranslation_userExerciseExists,
  'USER_EXERCISE_NOT_CREATED_BY_USER': (final uiStrings) => uiStrings.errorTranslation_userExerciseNotCreatedByUser,
  'SET_LOG_POSITION_CHANGE_FORBIDDEN': (final uiStrings) => uiStrings.errorTranslation_setLogPositionChangeForbidden,
  'WORKOUT_LOG_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_workoutLogNotFound,
  'EXERCISE_LOG_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_exerciseLogNotFound,
  'SET_LOG_NOT_FOUND': (final uiStrings) => uiStrings.errorTranslation_setLogNotFound,
  'WORKOUT_LOG_NOT_BY_USER': (final uiStrings) => uiStrings.errorTranslation_workoutLogNotByUser,
  'EXERCISE_LOG_NOT_IN_WORKOUT_LOG': (final uiStrings) => uiStrings.errorTranslation_exerciseLogNotInWorkoutLog,
  'SET_LOG_NOT_IN_EXERCISE_LOG': (final uiStrings) => uiStrings.errorTranslation_setLogNotInExerciseLog,
  'INVALID_LOGGING_TYPE': (final uiStrings) => uiStrings.errorTranslation_invalidLoggingType,
  'POSITION_MAP_INVALID': (final uiStrings) => uiStrings.errorTranslation_positionMapInvalid,
  'POSITION_MAP_NOT_UNIQUE': (final uiStrings) => uiStrings.errorTranslation_positionMapNotUnique,
  'UNDEFINED_ERROR': (final uiStrings) => uiStrings.errorTranslation_undefinedError,
  'invalid-email': (final uiStrings) => uiStrings.errorTranslation_invalidEmail,
  'invalid-operation-not-allowed': (final uiStrings) => uiStrings.errorTranslation_operationNotAllowed,
  'user-disabled': (final uiStrings) => uiStrings.errorTranslation_userDisabled,
  'user-not-found': (final uiStrings) => uiStrings.errorTranslation_userNotFound,
  'wrong-password': (final uiStrings) => uiStrings.errorTranslation_wrongPassword,
};

/// A mixin which handles the translation of or mapping from server-specific error keys to localized strings.
/// Example:
/// ```dart
/// class ExampleWidget with StringLocalizer, ErrorKeyTranslator {
///   void build(BuildContext context) {
///     final uiStrings = getLocalizedStrings();
///     final errorKey = failingMethodReturningErrorKey();
///     final localizedErrorMessage = translate(uiStrings, errorKey);
///     return Center(child: Text('Could not execute method: $localizedErrorMessage');
///   }
/// }
/// ```
mixin ErrorKeyTranslator {
  /// Translates a given `errorKey` to a localized error message string. If there is no localized error message deposited for the given `errorKey`,
  /// then `fallback` is returned, if specified. If `fallback` is not specified, the value of the localized string `errorTranslation_undefinedError`
  /// is used instead.
  String translate(final StringLocalizations uiStrings, final String errorKey, {String? fallback}) {
    fallback ??= uiStrings.errorTranslation_undefinedError;
    return translateOrNull(uiStrings, errorKey) ?? fallback;
  }

  /// Translates a given `errorKey` to a localized error message string. If there is no localized error message deposited for the given `errorKey`,
  /// then `null` is returned.
  String? translateOrNull(final StringLocalizations uiStrings, final String errorKey) {
    _logger.i('Trying to translate error key "$errorKey"');
    final errorLocator = _errorKeys[errorKey];
    return errorLocator != null ? errorLocator(uiStrings) : null;
  }
}
