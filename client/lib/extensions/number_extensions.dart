extension NumberExtensions on int {
  /// Returns this instance and [word], concatenated using a space, in the correct grammatical number (singular/plural), depending on the value of the
  /// current instance. More specifically, adds a "plural-s" if the current instance is not equal to 1, otherwise it does not.
  /// Examples:
  /// ```
  /// 0.toNumberString('house') => '0 houses'
  /// 1.toNumberString('house') => '1 house'
  /// 3.toNumberString('house') => '3 houses'
  /// ```
  String toNumberString(String word) {
    return '$this $word${this != 1 ? 's' : ''}';
  }
}
