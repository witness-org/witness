import 'package:client/extensions/date_time_extensions.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('dateOnly', () {
    test('should return a date of the same day, with time set to midnight', () {
      final _inputDate = DateTime(2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = DateTime(2021, 12, 3, 0, 0, 0, 0, 0);
      final _outputDate = _inputDate.dateOnly();
      expect(_outputDate, _expectedDate);
    });
  });

  group('addYears', () {
    test('should return the same date if 0 year are added', () {
      final _inputDate = DateTime(2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = DateTime(2021, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.addYears(0);
      expect(_outputDate, _expectedDate);
    });

    test('should return a date with equal components, but increased year', () {
      final _inputDate = DateTime(2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = DateTime(2023, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.addYears(2);
      expect(_outputDate, _expectedDate);
    });
  });

  group('subtractYears', () {
    test('should return the same date if 0 year are subtracted', () {
      final _inputDate = DateTime(2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = DateTime(2021, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.subtractYears(0);
      expect(_outputDate, _expectedDate);
    });

    test('should return a date with equal components, but increased year', () {
      final _inputDate = DateTime(2021, 12, 3, 14, 46, 23, 123, 7772);
      final _expectedDate = DateTime(2019, 12, 3, 14, 46, 23, 123, 7772);
      final _outputDate = _inputDate.subtractYears(2);
      expect(_outputDate, _expectedDate);
    });
  });
}
