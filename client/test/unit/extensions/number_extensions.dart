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

  group(getPrefixedGroupName(_sutName, 'gInKg'), () {
    test("1000g should be 1kg", () {
      expect(1000.gInKg, 1.0);
    });

    test("5270g should be 5.27kg", () {
      expect(5270.gInKg, 5.27);
    });

    test("-40000g should be -40kg", () {
      expect(-40000.gInKg, -40.0);
    });
  });

  group(getPrefixedGroupName(_sutName, 'kgInG'), () {
    test("1kg should be 1000g", () {
      expect(1.0.kgInG, 1000);
    });

    test("5.27kg should be 5270g", () {
      expect(5.27.kgInG, 5270);
    });

    test("-40kg should be -40000g", () {
      expect(-40.0.kgInG, -40000);
    });

    test("1.2345kg should be 1235g", () {
      expect(1.2345.kgInG, 1235);
    });

    test("1.2342kg should be 1234g", () {
      expect(1.2342.kgInG, 1234);
    });
  });
}
