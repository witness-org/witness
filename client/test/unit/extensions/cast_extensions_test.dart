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
      final int? _int = obj.castOrNull<int>();
      expect(_int, 1);
    });

    test('should return null on string', () {
      const Object? obj = 'a';
      final int? _int = obj.castOrNull<int>();
      expect(_int, null);
    });

    test('should return null on null', () {
      const Object? obj = null;
      final int? _int = obj.castOrNull<int>();
      expect(_int, null);
    });
  });

  group(getPrefixedGroupName(_sutName, 'castOrFallback<int>'), () {
    test('should return int on int', () {
      const Object? obj = 1;
      final int _int = obj.castOrFallback<int>(2);
      expect(_int, 1);
    });

    test('should return fallback on string', () {
      const Object? obj = 'a';
      final int? _int = obj.castOrFallback<int>(2);
      expect(_int, 2);
    });

    test('should return fallback on null', () {
      const Object? obj = null;
      final _int = obj.castOrFallback<int>(2);
      expect(_int, 2);
    });
  });

  group(getPrefixedGroupName(_sutName, 'castOrThrow<int>'), () {
    test('should return int on int', () {
      const Object? obj = 1;
      final int _int = obj.castOrThrow<int>();
      expect(_int, 1);
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
