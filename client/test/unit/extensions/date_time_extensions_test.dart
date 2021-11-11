import 'package:client/extensions/date_time_extensions.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:timezone/data/latest.dart';
import 'package:timezone/timezone.dart';

import '../../common/test_helpers.dart';

final vienna = getLocation('Europe/Vienna');
const _sutName = 'date_time_extensions';

void main() {
  initializeTimeZones();

  group(getPrefixedGroupName(_sutName, 'addYears'), () {
    test('should return the same date if 0 year are added', () {
      final _inputDate = TZDateTime(vienna, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = TZDateTime(vienna, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.addYears(0);
      expect(_outputDate, _expectedDate);
    });

    test('should return a date with equal components, but increased year', () {
      final _inputDate = TZDateTime(vienna, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = TZDateTime(vienna, 2023, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.addYears(2);
      expect(_outputDate, _expectedDate);
    });
  });

  group(getPrefixedGroupName(_sutName, 'subtractYears'), () {
    test('should return the same date if 0 year are subtracted', () {
      final _inputDate = TZDateTime(vienna, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = TZDateTime(vienna, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.subtractYears(0);
      expect(_outputDate, _expectedDate);
    });

    test('should return a date with equal components, but increased year', () {
      final _inputDate = TZDateTime(vienna, 2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = TZDateTime(vienna, 2019, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.subtractYears(2);
      expect(_outputDate, _expectedDate);
    });
  });
}
