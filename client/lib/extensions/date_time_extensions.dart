import 'package:date_format/date_format.dart';
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

  /// Retrieves a string representation from the current instance, leaving the time components untouched.
  // TODO(lea): test, write documentation and refactor (format should be a function parameter)
  String getStringRepresentation({final String? yesterdayText, final String? todayText}) {
    final now = DateTime.now();
    final difference = DateTime(year, month, day).difference(DateTime(now.year, now.month, now.day)).inDays;

    if (difference == -1 && yesterdayText != null) {
      return yesterdayText;
    } else if (difference == 0 && todayText != null) {
      return todayText;
    }

    final formatList = difference > 365 ? [MM, ' ', dd, ', ', yyyy] : [MM, ' ', dd]; // add year if log is from over a year ago
    return formatDate(this, formatList);
  }
}
