/// Provides methods that facilitate [Map] handling.
extension MapExtensions<K, V> on Map<K, V> {
  /// Filters the map entries by the given predicate and returns a list of the filtered map entries.
  Iterable<MapEntry<K, V>> where(final bool Function(MapEntry<K, V> entry) predicate) {
    return entries.where((final element) => predicate(element));
  }

  /// Filters the map entries by the given predicate and returns a list of the keys of the filtered map entries.
  Iterable<K> whereKeys(final bool Function(MapEntry<K, V> entry) predicate) {
    return entries.where(predicate).map((final e) => e.key);
  }

  /// Filters the map entries by the given predicate and returns a list of the values of the filtered map entries.
  Iterable<V> whereValues(final bool Function(MapEntry<K, V> entry) predicate) {
    return entries.where(predicate).map((final e) => e.value);
  }

  /// Checks whether a given predicate holds for any entry of the map.
  bool holdsForAny(final bool Function(MapEntry<K, V> entry) predicate) {
    return entries.any(predicate);
  }
}

/// Provides methods that facilitate handling of nullable [Map]s.
extension NullableMapExtensions<K, V> on Map<K, V>? {
  /// Returns the current instance if it is not `null`, otherwise an empty [Map] with the same generic types.
  Map<K, V> orEmpty() {
    return this ?? <K, V>{};
  }
}
