extension IntExtensions on int {
  /// Returns this instance and [word], concatenated using a space, in the correct grammatical number (singular/plural), depending on the value of the
  /// current instance. More specifically, adds a "plural-s" if the current instance is not equal to 1, otherwise it does not.
  /// Examples:
  /// ```
  /// 0.toNumberString('house') => '0 houses'
  /// 1.toNumberString('house') => '1 house'
  /// 3.toNumberString('house') => '3 houses'
  /// ```
  ///
  /// Note: This method is only suitable for English locales.
  String toNumberString(final String word) {
    return '$this $word${this != 1 ? 's' : ''}';
  }

  /// Interprets this instance in grams and returns the equivalent value in kilograms.
  double get gInKg {
    return this / 1000;
  }
}

extension DoubleExtensions on double {
  /// Interprets this instance in kilograms and returns the equivalent value in grams. If there are more than three decimals, the value is rounded
  /// using the rounding half away from zero strategy (e.g. 1.2345kg will be 1235g, 1.2341kg will be 1234g).
  int get kgInG {
    return (this * 1000).round();
  }
}
