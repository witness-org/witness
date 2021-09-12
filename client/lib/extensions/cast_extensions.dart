/// Provides extension methods on [Object?] that facilitate common tasks when casting types.
/// It encapsulates logic around hard casts ("x as T", "y as T?") such as returning null or
/// a fallback value asl well as throwing an exception if the cast is not possible.
extension CastExtensions on Object? {
  /// Tries to cast to ```T```. If the cast is not possible, `null` is returned.
  T? castOrNull<T>() {
    return this is T ? (this as T) : null;
  }

  /// Tries to cast to ```T```. If the cast is not possible, [fallback] is returned.
  T castOrFallback<T>(T fallback) {
    return this is T ? (this as T) : fallback;
  }

  /// Tries to cast to ```T```. If the cast is not possible, standard Dart behaviour ensues, i.e. a [TypeError] is thrown.
  T castOrThrow<T>() {
    return this as T;
  }
}
