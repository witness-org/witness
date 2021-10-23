/// Represents a response from the server to a request.
/// [success] is the object that was returned (may be null), [error] is the object that indicates an error (e.g. a string containing the error
/// message). [error] is `null` if the request completed successfully, i.e. no error occurred.
class ServerResponse<TSuccess, TError> {
  const ServerResponse(this.success, this.error);

  const ServerResponse.success(final TSuccess success) : this(success, null);

  const ServerResponse.failure(final TError error) : this(null, error);

  final TSuccess? success;
  final TError? error;

  /// Returns `true` if [error] is not `null`.
  bool get isError => error != null;

  /// Returns `true` if [error] is not `null` and [success] is `null`.
  /// A successful response with the [success] property being `null` might, for instance, be returned to an HTTP DELETE request, where the server does
  /// not send a response body (status code only).
  bool get isSuccessNoResponse => error == null && success == null;

  /// Returns `true` if neither [error] nor [success] is `null`.
  /// Note that when accessing the [success] field after this check in a context where it must not be null, type promotion does not succeed.
  /// Nevertheless, if this getter returns `true`, it is acceptable to use the bang operator (`!`) on [success] in a non-nullable context.
  bool get isSuccessAndResponse => error == null && success != null;
}
