import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/widgets/app_routing.dart' as app_routing;
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flex_color_scheme/flex_color_scheme.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('app');

class WitnessClient extends StatefulWidget {
  const WitnessClient({final Key? key = const Key('root')}) : super(key: key);

  @override
  State<WitnessClient> createState() => _WitnessClientState();
}

class _WitnessClientState extends State<WitnessClient> with LogMessagePreparer, StringLocalizer {
  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return Consumer<AuthProvider>(
      builder: (final ctx, final auth, final _) => MaterialApp(
        localizationsDelegates: StringLocalizations.localizationsDelegates,
        supportedLocales: StringLocalizations.supportedLocales,
        onGenerateTitle: (final titleContext) => getLocalizedStrings(titleContext).appTitle,
        theme: FlexThemeData.light(scheme: FlexScheme.bigStone),
        darkTheme: FlexThemeData.dark(scheme: FlexScheme.bigStone),
        themeMode: ThemeMode.system,
        onGenerateRoute: (final routeSettings) => app_routing.selectRoute(routeSettings, auth),
      ),
    );
  }
}
