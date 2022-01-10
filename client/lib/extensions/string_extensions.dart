/// Provides methods that facilitate [String] handling.
extension StringExtensions on String {
  /// Checks whether given [String] is blank, i.e. is empty or consists only of whitespaces.
  bool get isBlank {
    return trim().isEmpty;
  }

  /// Joins a list of [String] instances with [separator] as separator, if not null. Elements that
  /// are null are ignored and do not contribute to the return value. Examples:
  /// ```
  /// StringExtensions.join(['string1', 'string2'], separator: ' ') == 'string1 string2'
  /// StringExtensions.join(['string1', null, 'string2']) == 'string1string2'
  /// StringExtensions.join([], separator: ' ') == ''
  /// StringExtensions.join([null, null, null], separator: '-') == ''
  /// ```
  static String join(final List<String?> strings, {final String? separator}) {
    final buffer = StringBuffer();

    for (var i = 0; i < strings.length; i++) {
      if (strings[i] != null) {
        buffer.write(strings[i]);

        if (separator != null && i != strings.length - 1) {
          buffer.write(separator);
        }
      }
    }

    return buffer.toString();
  }
}

extension NullableStringExtensions on String? {
  /// Return `true` if the given instance is empty or is blank (consists only of whitespaces), otherwise false.
  bool get isNullOrBlank {
    return this == null || this!.isBlank;
  }
}
