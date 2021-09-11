import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class MainAppBar extends StatelessWidget implements PreferredSizeWidget {
  final double height;

  const MainAppBar({Key? key, this.height = kToolbarHeight}) : super(key: key);

  @override
  Size get preferredSize {
    return Size.fromHeight(height);
  }

  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: Text(AppLocalizations.of(context)!.appTitle),
      actions: [
        IconButton(
          onPressed: () {},
          icon: Icon(Icons.calendar_today),
        ),
        IconButton(
          onPressed: () {},
          icon: Icon(Icons.account_circle),
        ),
      ],
    );
  }
}
