import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('settings_screen');

class SettingsScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const SettingsScreen({final Key? key}) : super(key: key);

  static const routeName = '/settings';

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Scaffold(
      appBar: const MainAppBar(),
      drawer: const AppDrawer(),
      body: Center(
        child: Text(uiStrings.settingsScreen_placeholder_text),
      ),
    );
  }
}
