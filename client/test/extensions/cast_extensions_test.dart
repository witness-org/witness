import 'package:client/extensions/cast_extensions.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:matcher/matcher.dart' as matchers;

void main() {
  group('castOrNull<int>', () {
    test('should return int on int', () {
      Object? obj = 1;
      int? _int = obj.castOrNull<int>();
      expect(_int, 1);
    });

    test('should return null on string', () {
      // ignore: avoid_init_to_null, null-initializer is required to test functionality
      Object? obj = 'a';
      int? _int = obj.castOrNull<int>();
      expect(_int, null);
    });

    test('should return null on null', () {
      // ignore: avoid_init_to_null, null-initializer is required to test functionality
      Object? obj = null;
      int? _int = obj.castOrNull<int>();
      expect(_int, null);
    });
  });

  group('castOrFallback<int>', () {
    test('should return int on int', () {
      Object? obj = 1;
      int _int = obj.castOrFallback<int>(2);
      expect(_int, 1);
    });

    test('should return fallback on string', () {
      // ignore: avoid_init_to_null, null-initializer is required to test functionality
      Object? obj = 'a';
      int? _int = obj.castOrFallback<int>(2);
      expect(_int, 2);
    });

    test('should return fallback on null', () {
      // ignore: avoid_init_to_null, null-initializer is required to test functionality
      Object? obj = null;
      int _int = obj.castOrFallback<int>(2);
      expect(_int, 2);
    });
  });

  group('castOrThrow<int>', () {
    test('should return int on int', () {
      Object? obj = 1;
      int _int = obj.castOrThrow<int>();
      expect(_int, 1);
    });

    test('should throw on string', () {
      Object? obj = 'a';
      expect(() => obj.castOrThrow<int>(), throwsA(matchers.isA<TypeError>()));
    });

    test('should throw on null', () {
      // ignore: avoid_init_to_null, null-initializer is required to test functionality
      Object? obj = null;
      expect(() => obj.castOrThrow<int>(), throwsA(matchers.isA<TypeError>()));
    });
  });
}
