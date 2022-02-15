/// Provides methods that facilitate handling of nullable [List]s.
extension NullableListExtensions<T> on List<T>? {
  /// Returns the current instance if it is not `null`, otherwise an empty [List] of the same type.
  List<T> orEmpty() {
    return this ?? <T>[];
  }
}
