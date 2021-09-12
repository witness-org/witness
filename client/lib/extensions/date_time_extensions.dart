import 'package:flutter/material.dart';

/// Encapsulates commonly used operations on [DateTime] objects.
extension DateTimeExtensions on DateTime {
  /// Returns a [DateTime] with the date of the current instance, but the time-part is set to midnight.
  DateTime dateOnly() {
    return DateUtils.dateOnly(this);
  }

  /// Adds [years] years to the current instance, leaving the time components untouched.
  DateTime addYears(int years) {
    return DateTime(this.year + years, this.month, this.day, this.hour, this.minute, this.second, this.millisecond, this.microsecond);
  }

  /// Subtracts [years] years from the current instance, leaving the time components untouched.
  DateTime subtractYears(int years) {
    return this.addYears(-years);
  }
}
