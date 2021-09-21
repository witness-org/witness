import 'package:client/extensions/string_extensions.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('join', () {
    test('should join strings with given separator', () {
      const strings = ['string1', 'string2'];
      const String? separator = ' '; // ignore: unnecessary_nullable_for_final_variable_declarations
      expect(StringExtensions.join(strings, separator: separator), 'string1 string2');
    });

    test('does not insert separator if it is null', () {
      final strings = ['string1', 'string2'];
      expect(StringExtensions.join(strings), 'string1string2');
    });

    test('should join strings with given separator, skipping the null-value', () {
      const strings = ['string1', null, 'string2'];
      const String? separator = ' '; // ignore: unnecessary_nullable_for_final_variable_declarations
      expect(StringExtensions.join(strings, separator: separator), 'string1 string2');
    });

    test('empty list of strings should yield empty string, regardless of separator', () {
      expect(StringExtensions.join([], separator: ' '), '');
      expect(StringExtensions.join([], separator: ''), '');
      expect(StringExtensions.join([], separator: null), '');
    });

    test('list with only null-values should yield empty string, regardless of separator', () {
      expect(StringExtensions.join([null, null, null], separator: ' '), '');
      expect(StringExtensions.join([null, null, null], separator: ''), '');
      expect(StringExtensions.join([null, null, null], separator: null), '');
    });
  });
}
