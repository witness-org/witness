import 'package:date_format/date_format.dart';
import 'package:timezone/timezone.dart';

final vienna = getLocation('Europe/Vienna');

/// Encapsulates commonly used operations on [TZDateTime] objects.
extension DateTimeExtensions on TZDateTime {
  /// Adds [years] years to the current instance, leaving the time components untouched.
  TZDateTime addYears(final int years) {
    return TZDateTime(vienna, year + years, month, day, hour, minute, second, millisecond, microsecond);
  }

  /// Subtracts [years] years from the current instance, leaving the time components untouched.
  TZDateTime subtractYears(final int years) {
    return addYears(-years);
  }

  /// Retrieves a string representation from the current instance, leaving the time components untouched.
  // TODO(lea): test, write documentation and refactor (use intl)
  String getStringRepresentation({final String? yesterdayText, final String? todayText}) {
    final now = TZDateTime.now(vienna);
    final difference = TZDateTime(vienna, year, month, day).difference(TZDateTime(vienna, now.year, now.month, now.day)).inDays;

    if (difference == -1 && yesterdayText != null) {
      return yesterdayText;
    } else if (difference == 0 && todayText != null) {
      return todayText;
    }

    final formatList = difference > 365 ? [MM, ' ', dd, ', ', yyyy] : [MM, ' ', dd]; // add year if log is from over a year ago
    return formatDate(this, formatList);
  }
}
