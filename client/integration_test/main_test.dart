// https://flutter.dev/docs/testing/integration-tests

// The application under test.
import 'package:client/main.dart' as app;
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  T getWidgetByKey<T extends Widget>(String key, WidgetTester tester) {
    final Finder cnt = find.byKey(Key(key));
    return tester.firstWidget<T>(cnt);
  }

  group('end-to-end test: ', () {
    testWidgets('root element exists', (WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();

      // find any widget with key root - method would throw (test would fail) if not available
      getWidgetByKey<Widget>("root", tester);
    });
  });
}
