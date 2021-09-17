/// Provides methods that facilitate [String] handling.
extension StringExtensions on String {
  /// Joins a list of [String] instances with [separator] as separator, if not null. Elements that
  /// are null are ignored and do not contribute to return value. Examples:
  /// ```
  /// StringExtensions.join(['string1', 'string2'], separator: ' ') == 'string1 string2'
  /// StringExtensions.join(['string1', null, 'string2']) == 'string1string2'
  /// StringExtensions.join([], separator: ' ') == ''
  /// StringExtensions.join([null, null, null], separator: '-') == ''
  /// ```
  static String join(List<String?> strings, {String? separator}) {
    final buffer = new StringBuffer();

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
