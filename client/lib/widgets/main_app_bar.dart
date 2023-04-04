import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/workouts/workout_log_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:timezone/timezone.dart';

final _logger = getLogger('main_app_bar');

class MainAppBar extends StatelessWidget with LogMessagePreparer, StringLocalizer implements PreferredSizeWidget {
  const MainAppBar({this.preferredHeight = kToolbarHeight, this.preferredTitle, this.currentlyViewedDate, final Key? key}) : super(key: key);

  final double preferredHeight;
  final String? preferredTitle;
  final TZDateTime? currentlyViewedDate;

  @override
  Size get preferredSize {
    return Size.fromHeight(preferredHeight);
  }

  void _showTokenDialog(final AuthProvider auth, final BuildContext context, final StringLocalizations uiStrings) {
    auth.getToken().then((final token) {
      showDialog<void>(
        context: context,
        builder: (final ctx) => AlertDialog(
          title: Text(uiStrings.mainAppBar_generateToken_dialog_title),
          content: SelectableText(token ?? uiStrings.mainAppBar_generateToken_dialog_fallback),
          actions: [
            TextButton(
              onPressed: () => Clipboard.setData(ClipboardData(text: token)),
              child: Text(uiStrings.mainAppBar_generateToken_copy),
            ),
            TextButton(
              onPressed: () => Navigator.pop(ctx, true),
              child: Text(uiStrings.mainAppBar_generateToken_ok),
            ),
          ],
        ),
      );
    });
  }

  void _selectDate(final BuildContext context, final String dialogTitle, final TZDateTime currentlyViewedDate) {
    showDialog<void>(
      context: context,
      builder: (final BuildContext context) {
        return DialogHelper.getDatePicker(
          context,
          dialogTitle,
          currentlyViewedDate,
          (final pickedDate) {
            final date = pickedDate.onlyTZDate();
            Navigator.of(context).pushReplacementNamed(WorkoutLogScreen.routeName, arguments: date);
          },
        );
      },
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    final auth = Provider.of<AuthProvider>(context, listen: false);
    return AppBar(
      title: Text(preferredTitle ?? uiStrings.appTitle),
      actions: [
        if (currentlyViewedDate != null)
          IconButton(
            onPressed: () => _selectDate(context, uiStrings.dialogHelper_datePickerDialog_defaultTitle, currentlyViewedDate!),
            icon: const Icon(Icons.calendar_today),
            tooltip: uiStrings.mainAppBar_action_selectDay,
          ),
        IconButton(
          onPressed: () => _showTokenDialog(auth, context, uiStrings),
          icon: const Icon(Icons.vpn_key),
          tooltip: uiStrings.mainAppBar_generateToken,
        ),
        IconButton(
          onPressed: () => Navigator.of(context).pushReplacementNamed(SettingsScreen.routeName),
          icon: const Icon(Icons.account_circle),
          tooltip: uiStrings.mainAppBar_settings,
        ),
      ],
    );
  }
}
