import 'package:client/extensions/date_time_extensions.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:intl/date_symbol_data_local.dart';
import 'package:intl/intl.dart';
import 'package:timezone/data/latest.dart';
import 'package:timezone/timezone.dart';
import 'package:timezone/timezone.dart' as tz;

import '../../common/test_helpers.dart';

final _localTimezone = tz.local;
final _dateFormatWithoutYear = DateFormat.MMMMd();
final _dateFormatWithYear = DateFormat.yMMMMd();
const _sutName = 'date_time_extensions';

void main() {
  initializeTimeZones();
  initializeDateFormatting('en', null);
  tz.setLocalLocation(tz.getLocation('America/Detroit'));

  group(getPrefixedGroupName(_sutName, 'onlyDate'), () {
    test('should return a date of the same day, with time set to midnight', () {
      final inputDate = TZDateTime(_localTimezone, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final expectedDate = TZDateTime(_localTimezone, 2021, 12, 3, 0, 0, 0, 0);
      final outputDate = inputDate.onlyDate();
      expect(outputDate, expectedDate);
    });
  });

  group(getPrefixedGroupName(_sutName, 'addYears'), () {
    test('should return the same date if 0 years are added', () {
      final inputDate = TZDateTime(_localTimezone, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final expectedDate = TZDateTime(_localTimezone, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final outputDate = inputDate.addYears(0);
      expect(outputDate, expectedDate);
    });

    test('should return a date with equal components, but increased year', () {
      final inputDate = TZDateTime(_localTimezone, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final expectedDate = TZDateTime(_localTimezone, 2023, 12, 3, 14, 46, 23, 123, 7772);
      final outputDate = inputDate.addYears(2);
      expect(outputDate, expectedDate);
    });
  });

  group(getPrefixedGroupName(_sutName, 'subtractYears'), () {
    test('should return the same date if 0 years are subtracted', () {
      final inputDate = TZDateTime(_localTimezone, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final expectedDate = TZDateTime(_localTimezone, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final outputDate = inputDate.subtractYears(0);
      expect(outputDate, expectedDate);
    });

    test('should return a date with equal components, but increased year', () {
      final inputDate = TZDateTime(_localTimezone, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final expectedDate = TZDateTime(_localTimezone, 2019, 12, 3, 14, 46, 23, 123, 7772);
      final outputDate = inputDate.subtractYears(2);
      expect(outputDate, expectedDate);
    });
  });

  group(getPrefixedGroupName(_sutName, 'getStringRepresentation'), () {
    test('should return date string without year if no text for today', () {
      final inputDate = TZDateTime.now(_localTimezone);
      final outputDate = inputDate.getStringRepresentation();
      expect(outputDate, _dateFormatWithoutYear.format(inputDate));
    });

    test('should return date string without year if no text for yesterday', () {
      final inputDate = TZDateTime.now(_localTimezone).subtract(const Duration(days: 1));
      final outputDate = inputDate.getStringRepresentation();
      expect(outputDate, _dateFormatWithoutYear.format(inputDate));
    });

    test('should return specified string if text for today', () {
      final inputDate = TZDateTime.now(_localTimezone);
      const todayText = 'today';
      final outputDate = inputDate.getStringRepresentation(todayText: todayText);
      expect(outputDate, todayText);
    });

    test('should return specified string if text for yesterday', () {
      final inputDate = TZDateTime.now(_localTimezone).subtract(const Duration(days: 1));
      const yesterdayText = 'yesterday';
      final outputDate = inputDate.getStringRepresentation(yesterdayText: yesterdayText);
      expect(outputDate, yesterdayText);
    });

    test('should return date string with year if date a year ago', () {
      final inputDate = TZDateTime.now(_localTimezone).subtract(const Duration(days: 365));
      final outputDate = inputDate.getStringRepresentation();
      expect(outputDate, _dateFormatWithYear.format(inputDate));
    });

    test('should return date string with year if date more than a year ago', () {
      final inputDate = TZDateTime.now(_localTimezone).subtract(const Duration(days: 366));
      final outputDate = inputDate.getStringRepresentation();
      expect(outputDate, _dateFormatWithYear.format(inputDate));
    });
  });
}
