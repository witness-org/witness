// https://flutter.dev/docs/testing/integration-tests

// The application under test.
import 'package:client/main.dart' as app;
import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  T getWidgetByKey<T extends Widget>(final String key, final WidgetTester tester) {
    final Finder cnt = find.byKey(Key(key));
    return tester.firstWidget<T>(cnt);
  }

  group('end-to-end test', () {
    testWidgets('counter starts at 0', (final WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();

      // find the counter Text widget by its key
      final counterText = getWidgetByKey<Text>("counter", tester);

      expect(counterText.data, "0");
    });

    testWidgets('increments counter on tapping floating button', (final WidgetTester tester) async {
      app.main();
      await tester.pumpAndSettle();

      // elements can also by found by attributes different from key
      // here, we find the floating button based on its tooltip
      final Finder button = find.byTooltip('Increment');

      // Emulate a tap on the floating action button.
      await tester.tap(button);
      await tester.tap(button);

      // wait until all frames are rendered and all animations have been completed
      await tester.pumpAndSettle();

      // find the counter Text widget by its key
      final counterText = getWidgetByKey<Text>("counter", tester);

      expect(counterText.data, "2");
    });
  });
}
