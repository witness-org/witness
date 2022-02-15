import 'package:client/extensions/map_extensions.dart';
import 'package:collection/collection.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../common/test_helpers.dart';

const _sutName = 'map_extensions';

final _unorderedIterableEqualsInt = const UnorderedIterableEquality<int>().equals;
final _unorderedIterableEqualsString = const UnorderedIterableEquality<String>().equals;

void main() {
  group(getPrefixedGroupName(_sutName, 'MapExtensions'), () {
    // Since MapEntry objects cannot be compared (see https://github.com/dart-lang/sdk/issues/32559), a list of the returned map entry keys
    // and values, respectively, is collected and checked for equality.
    group('where', () {
      test('with TRUE predicate should return all map entries', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => true;

        final result = map.where(predicate);
        final keyList = result.map((final entry) => entry.key).toList();
        final valueList = result.map((final entry) => entry.value).toList();

        expect(_unorderedIterableEqualsInt(keyList, const [1, 2, 3]), true);
        expect(_unorderedIterableEqualsString(valueList, const ['test1', 'test2', 'test3']), true);
      });

      test('with FALSE predicate should return no map entries', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => false;

        final result = map.where(predicate);
        final keyList = result.map((final entry) => entry.key).toList();
        final valueList = result.map((final entry) => entry.value).toList();

        expect(_unorderedIterableEqualsInt(keyList, []), true);
        expect(_unorderedIterableEqualsString(valueList, []), true);
      });

      test('with <= predicate should return list of filtered map entries', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => entry.key <= 2;

        final result = map.where(predicate);
        final keyList = result.map((final entry) => entry.key).toList();
        final valueList = result.map((final entry) => entry.value).toList();

        expect(_unorderedIterableEqualsInt(keyList, const [1, 2]), true);
        expect(_unorderedIterableEqualsString(valueList, const ['test1', 'test2']), true);
      });
    });

    group('whereKeys', () {
      test('with TRUE predicate should return all map keys', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => true;

        expect(_unorderedIterableEqualsInt(map.whereKeys(predicate), const [1, 2, 3]), true);
      });

      test('with FALSE predicate should return no map keys', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => false;

        expect(_unorderedIterableEqualsInt(map.whereKeys(predicate), []), true);
      });

      test('with <= predicate should return list of filtered map keys', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => entry.key <= 2;

        expect(_unorderedIterableEqualsInt(map.whereKeys(predicate), const [1, 2]), true);
      });
    });

    group('whereValues', () {
      test('with TRUE predicate should return all map values', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => true;

        expect(_unorderedIterableEqualsString(map.whereValues(predicate), const ['test1', 'test2', 'test3']), true);
      });

      test('with FALSE predicate should return no map values', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => false;

        expect(_unorderedIterableEqualsString(map.whereValues(predicate), []), true);
      });

      test('with <= predicate should return list of filtered map values', () {
        const map = {1: 'test1', 2: 'test2', 3: 'test3'};
        predicate(final MapEntry<int, String> entry) => entry.key <= 2;

        expect(_unorderedIterableEqualsString(map.whereValues(predicate), const ['test1', 'test2']), true);
      });
    });
  });

  group('holdsForAny', () {
    test('with TRUE predicate should return TRUE', () {
      const map = {1: 'test1', 2: 'test2', 3: 'test3'};
      predicate(final MapEntry<int, String> entry) => true;

      expect(map.holdsForAny(predicate), true);
    });

    test('with TRUE predicate should return FALSE', () {
      const map = {1: 'test1', 2: 'test2', 3: 'test3'};
      predicate(final MapEntry<int, String> entry) => false;

      expect(map.holdsForAny(predicate), false);
    });

    test('with <= predicate should return TRUE', () {
      const map = {1: 'test1', 2: 'test2', 3: 'test3'};
      predicate(final MapEntry<int, String> entry) => entry.key <= 2;

      expect(map.holdsForAny(predicate), true);
    });

    test('with > predicate should return FALSE', () {
      const map = {1: 'test1', 2: 'test2', 3: 'test3'};
      predicate(final MapEntry<int, String> entry) => entry.key > 3;

      expect(map.holdsForAny(predicate), false);
    });
  });

  group(getPrefixedGroupName(_sutName, 'NullableMapExtensions'), () {
    group('orEmpty', () {
      test('should return input map if it is empty', () {
        final Map<String, int> emptyMap = <String, int>{};
        final returnedMap = emptyMap.orEmpty();
        expect(returnedMap, emptyMap);
      });

      test('should return input map if it is non-empty', () {
        final Map<String, int> nonEmptyMap = {"key1": 23, "key2": 272, "key3": -273};
        final returnedMap = nonEmptyMap.orEmpty();
        expect(returnedMap, nonEmptyMap);
      });

      test('should return empty map if input is null', () {
        const Map<String, int>? nullMap = null;
        final returnedMap = nullMap.orEmpty();
        expect(returnedMap, <String, int>{});
      });
    });
  });
}
