import 'package:flutter/material.dart';

/// Encapsulates commonly used operations on [DateTime] objects.
extension DateTimeExtensions on DateTime {
  /// Returns a [DateTime] with the date of the current instance, but the time-part is set to midnight.
  DateTime dateOnly() {
    return DateUtils.dateOnly(this);
  }

  /// Adds [years] years to the current instance, leaving the time components untouched.
  DateTime addYears(final int years) {
    return DateTime(year + years, month, day, hour, minute, second, millisecond, microsecond);
  }

  /// Subtracts [years] years from the current instance, leaving the time components untouched.
  DateTime subtractYears(final int years) {
    return addYears(-years);
  }
}
