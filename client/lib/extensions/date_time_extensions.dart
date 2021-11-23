import 'package:intl/intl.dart';
import 'package:timezone/timezone.dart';
import 'package:timezone/timezone.dart' as tz;

/// Encapsulates commonly used operations on [TZDateTime] objects.
extension DateTimeExtensions on TZDateTime {
  /// Adds [years] years to the current instance, leaving the time components untouched.
  TZDateTime addYears(final int years) {
    return TZDateTime(tz.local, year + years, month, day, hour, minute, second, millisecond, microsecond);
  }

  /// Subtracts [years] years from the current instance, leaving the time components untouched.
  TZDateTime subtractYears(final int years) {
    return addYears(-years);
  }

  /// Retrieves a string representation from the current instance, leaving the time components untouched.
  // TODO(lea): test + write documentation
  // TODO(lea-raffael): localize date format?
  String getStringRepresentation({final String? yesterdayText, final String? todayText}) {
    final now = TZDateTime.now(tz.local);
    final difference = TZDateTime(tz.local, year, month, day).difference(TZDateTime(tz.local, now.year, now.month, now.day)).inDays;

    if (difference == -1 && yesterdayText != null) {
      return yesterdayText;
    } else if (difference == 0 && todayText != null) {
      return todayText;
    }

    final format = difference >= -365 ? DateFormat.MMMMd() : DateFormat.yMMMMd();
    return format.format(this);
  }
}
