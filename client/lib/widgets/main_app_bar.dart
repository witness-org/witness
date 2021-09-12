import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/workouts/workout_overview_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

final _logger = getLogger('main_app_bar');

class MainAppBar extends StatelessWidget implements PreferredSizeWidget {
  final double preferredHeight;
  final String? preferredTitle;
  final DateTime? currentlyViewedDate;

  @override
  Size get preferredSize {
    return Size.fromHeight(preferredHeight);
  }

  const MainAppBar({Key? key, this.preferredHeight = kToolbarHeight, this.preferredTitle, this.currentlyViewedDate}) : super(key: key);

  Future<void> _selectDate(BuildContext context) async {
    final referenceDate = currentlyViewedDate ?? DateTime.now();
    final pickedDate = await showDatePicker(
      context: context,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      initialDate: referenceDate,
      firstDate: referenceDate.subtractYears(1),
      lastDate: referenceDate.addYears(1),
    );

    if (pickedDate != null) {
      Navigator.of(context).pushReplacementNamed(WorkoutOverviewScreen.routeName, arguments: pickedDate.dateOnly());
    }
  }

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    final uiStrings = AppLocalizations.of(context)!;
    return AppBar(
      title: Text(preferredTitle ?? uiStrings.appTitle),
      actions: [
        IconButton(
          onPressed: () => _selectDate(context),
          icon: Icon(Icons.calendar_today),
          tooltip: uiStrings.mainAppBar_overview,
        ),
        IconButton(
          onPressed: () => Navigator.of(context).pushReplacementNamed(SettingsScreen.routeName),
          icon: Icon(Icons.account_circle),
          tooltip: uiStrings.mainAppBar_settings,
        ),
      ],
    );
  }
}
