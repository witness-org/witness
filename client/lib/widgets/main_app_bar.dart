import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('main_app_bar');

class MainAppBar extends StatelessWidget with LogMessagePreparer, StringLocalizer implements PreferredSizeWidget {
  const MainAppBar({final Key? key, final this.preferredHeight = kToolbarHeight, final this.preferredTitle, final this.currentlyViewedDate})
      : super(key: key);

  final double preferredHeight;
  final String? preferredTitle;
  final DateTime? currentlyViewedDate;

  @override
  Size get preferredSize {
    return Size.fromHeight(preferredHeight);
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return AppBar(
      title: Text(preferredTitle ?? uiStrings.appTitle),
      actions: [
        IconButton(
          onPressed: () => Navigator.of(context).pushReplacementNamed(SettingsScreen.routeName),
          icon: const Icon(Icons.account_circle),
          tooltip: uiStrings.mainAppBar_settings,
        ),
      ],
    );
  }
}
