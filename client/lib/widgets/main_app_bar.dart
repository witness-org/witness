import 'package:client/logging/logger_factory.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

final _logger = getLogger('main_app_bar');

class MainAppBar extends StatelessWidget implements PreferredSizeWidget {
  final double preferredHeight;
  final String? preferredTitle;

  const MainAppBar({Key? key, this.preferredHeight = kToolbarHeight, this.preferredTitle}) : super(key: key);

  @override
  Size get preferredSize {
    return Size.fromHeight(preferredHeight);
  }

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    final uiStrings = AppLocalizations.of(context)!;
    return AppBar(
      title: Text(preferredTitle ?? uiStrings.appTitle),
      actions: [
        IconButton(
          onPressed: () {},
          icon: Icon(Icons.calendar_today),
          tooltip: uiStrings.mainAppBar_overview,
        ),
        IconButton(
          onPressed: () {},
          icon: Icon(Icons.account_circle),
          tooltip: uiStrings.mainAppBar_settings,
        ),
      ],
    );
  }
}
