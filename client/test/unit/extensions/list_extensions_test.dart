import 'package:client/extensions/list_extensions.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../common/test_helpers.dart';

const _sutName = 'list_extensions';

void main() {
  group(getPrefixedGroupName(_sutName, 'NullableListExtensions'), () {
    group('orEmpty', () {
      test('should return input list if it is empty', () {
        final List<String> emptyList = <String>[];
        final returnedList = emptyList.orEmpty();
        expect(returnedList, emptyList);
      });

      test('should return input list if it is non-empty', () {
        final List<String> nonEmptyList = ['item1', 'item2', 'item3'];
        final returnedMap = nonEmptyList.orEmpty();
        expect(returnedMap, nonEmptyList);
      });

      test('should return empty list if input is null', () {
        const List<String>? nullList = null;
        final returnedList = nullList.orEmpty();
        expect(returnedList, <String>[]);
      });
    });
  });
}
