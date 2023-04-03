// ignore_for_file: unnecessary_nullable_for_final_variable_declarations

import 'package:client/extensions/cast_extensions.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:matcher/matcher.dart' as matchers;

import '../../common/test_helpers.dart';

const _sutName = 'cast_extensions';

void main() {
  group(getPrefixedGroupName(_sutName, 'castOrNull<int>'), () {
    test('should return int on int', () {
      const Object? obj = 1;
      final int? integer = obj.castOrNull<int>();
      expect(integer, 1);
    });

    test('should return null on string', () {
      const Object? obj = 'a';
      final int? integer = obj.castOrNull<int>();
      expect(integer, null);
    });

    test('should return null on null', () {
      const Object? obj = null;
      final int? integer = obj.castOrNull<int>();
      expect(integer, null);
    });
  });

  group(getPrefixedGroupName(_sutName, 'castOrFallback<int>'), () {
    test('should return int on int', () {
      const Object? obj = 1;
      final int integer = obj.castOrFallback<int>(2);
      expect(integer, 1);
    });

    test('should return fallback on string', () {
      const Object? obj = 'a';
      final int? integer = obj.castOrFallback<int>(2);
      expect(integer, 2);
    });

    test('should return fallback on null', () {
      const Object? obj = null;
      final integer = obj.castOrFallback<int>(2);
      expect(integer, 2);
    });
  });

  group(getPrefixedGroupName(_sutName, 'castOrThrow<int>'), () {
    test('should return int on int', () {
      const Object? obj = 1;
      final int integer = obj.castOrThrow<int>();
      expect(integer, 1);
    });

    test('should throw on string', () {
      const Object? obj = 'a';
      expect(() => obj.castOrThrow<int>(), throwsA(matchers.isA<TypeError>()));
    });

    test('should throw on null', () {
      const Object? obj = null;
      expect(() => obj.castOrThrow<int>(), throwsA(matchers.isA<TypeError>()));
    });
  });
}
