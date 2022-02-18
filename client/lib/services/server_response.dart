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
  /// Note that the compiler is not able to promote [error] to a non-nullable [TError] even if this getter returns `true`.
  /// Hence, it is acceptable to use the null-check operator (`!`) on [error] in a non-nullable context if [isError] returns `true`.
  bool get isError => error != null;

  /// Returns `true` if [error] is not `null` and [success] is `null`.
  /// A successful response with the [success] property being `null` might, for instance, be returned to an HTTP DELETE request, where the server does
  /// not send a response body (status code only).
  bool get isSuccessNoResponse => error == null && success == null;

  /// Returns `true` if neither [error] nor [success] is `null`.
  /// Note that the compiler is not able to promote [success] to a non-nullable [TSuccess] even if this getter returns `true`.
  /// Therefore, it is acceptable to use the null-check operator (`!`) on [success] in a non-nullable context if [isSuccessAndResponse] returns
  /// `true`.
  bool get isSuccessAndResponse => error == null && success != null;
}
