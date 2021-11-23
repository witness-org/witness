import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/widgets/app_routing.dart' as app_routing;
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';
import 'package:flutter_native_timezone/flutter_native_timezone.dart';
import 'package:provider/provider.dart';
import 'package:timezone/timezone.dart' as tz;

final _logger = getLogger('app');

class WitnessClient extends StatefulWidget {
  const WitnessClient({final Key? key}) : super(key: key);

  static ColorScheme _getLightColorTheme(final MaterialColor primaryColor, final MaterialColor secondaryColor) {
    final primarySwatch = primaryColor;
    final accentColor = secondaryColor;

    final bool primaryIsDark = ThemeData.estimateBrightnessForColor(primarySwatch) == Brightness.dark;
    final bool secondaryIsDark = ThemeData.estimateBrightnessForColor(accentColor) == Brightness.dark;

    return ColorScheme(
      primary: primarySwatch,
      primaryVariant: primarySwatch.shade700,
      secondary: accentColor,
      secondaryVariant: accentColor.shade700,
      surface: Colors.white,
      background: primarySwatch.shade200,
      error: Colors.red.shade700,
      onPrimary: primaryIsDark ? Colors.white : Colors.black,
      onSecondary: secondaryIsDark ? Colors.white : Colors.black,
      onSurface: Colors.black,
      onBackground: primaryIsDark ? Colors.white : Colors.black,
      onError: Colors.white,
      brightness: Brightness.light,
    );
  }

  @override
  State<WitnessClient> createState() => _WitnessClientState();
}

class _WitnessClientState extends State<WitnessClient> with LogMessagePreparer, StringLocalizer {
  final ColorScheme colorScheme = WitnessClient._getLightColorTheme(Colors.purple, Colors.amber);

  Future<void> _initLocalTimezone() async {
    final String local = await FlutterNativeTimezone.getLocalTimezone();
    tz.setLocalLocation(tz.getLocation(local));
  }

  @override
  void initState() {
    super.initState();
    _initLocalTimezone();
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return Consumer<AuthProvider>(
      builder: (final ctx, final auth, final _) => MaterialApp(
        localizationsDelegates: StringLocalizations.localizationsDelegates,
        supportedLocales: StringLocalizations.supportedLocales,
        onGenerateTitle: (final titleContext) => getLocalizedStrings(titleContext).appTitle,
        theme: ThemeData(
          primarySwatch: Colors.purple,
          colorScheme: colorScheme,
        ),
        // TODO(raffaelfoidl-leabrugger): Maybe define dark theme analogously to light theme.
        darkTheme: ThemeData.dark(),
        themeMode: ThemeMode.system,
        onGenerateRoute: (final routeSettings) => app_routing.selectRoute(routeSettings, auth),
      ),
    );
  }
}
