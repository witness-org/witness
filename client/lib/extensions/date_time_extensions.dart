import 'package:intl/intl.dart';
import 'package:timezone/timezone.dart';
import 'package:timezone/timezone.dart' as tz;

/// Encapsulates commonly used operations on [TZDateTime] objects.
extension TZDateTimeExtensions on TZDateTime {
  /// Returns a [TZDateTime] object containing only information about date
  TZDateTime onlyDate() {
    return TZDateTime(tz.local, year, month, day);
  }

  /// Adds [years] years to the current instance, leaving the time components untouched.
  TZDateTime addYears(final int years) {
    return TZDateTime(tz.local, year + years, month, day, hour, minute, second, millisecond, microsecond);
  }

  /// Subtracts [years] years from the current instance, leaving the time components untouched.
  TZDateTime subtractYears(final int years) {
    return addYears(-years);
  }

  /// Retrieves a string representation (containing the day and month) from the current instance, leaving the time components untouched.
  /// If [todayText] is provided and the day of the current instance is the current day, [todayText] is returned instead.
  /// If [yesterdayText] is provided and the day of the current instance is the day before the current date, [yesterdayText] is returned instead.
  /// If and only if the current instance is a date that lies more than 364 days before the current date, the year is also included in the string
  /// representation.
  String getStringRepresentation({final String? yesterdayText, final String? todayText}) {
    final now = TZDateTime.now(tz.local);
    final difference = onlyDate().difference(TZDateTime(tz.local, now.year, now.month, now.day)).inDays;

    if (difference == -1 && yesterdayText != null) {
      return yesterdayText;
    } else if (difference == 0 && todayText != null) {
      return todayText;
    }

    final format = difference > -365 ? DateFormat.MMMMd('en') : DateFormat.yMMMMd('en'); // TODO(leabrugger-raffaelfoidl): localize date format
    return format.format(this);
  }
}

/// Encapsulates commonly used operations on [DateTime] objects.
extension DateTimeExtensions on DateTime {
  /// Constructs a [TZDateTime] object from a [DateTime] instance containing only information about date.
  TZDateTime onlyTZDate() {
    return TZDateTime.local(year, month, day);
  }
}
