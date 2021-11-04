import 'package:client/extensions/number_extensions.dart';
import 'package:flutter_test/flutter_test.dart';

import '../../common/test_helpers.dart';

const _sutName = 'number_extensions';

void main() {
  group(getPrefixedGroupName(_sutName, 'toNumberString'), () {
    test("should append 's' if int is 0", () {
      expect(0.toNumberString('house'), '0 houses');
    });

    test(getPrefixedGroupName(_sutName, 'should append "s" if int is -0'), () {
      expect((-0).toNumberString('house'), '0 houses');
    });

    test(getPrefixedGroupName(_sutName, 'should not append "s" if int is 1'), () {
      expect(1.toNumberString('house'), '1 house');
    });

    test(getPrefixedGroupName(_sutName, 'should append "s" if int is 3'), () {
      expect(3.toNumberString('house'), '3 houses');
    });
  });
}
