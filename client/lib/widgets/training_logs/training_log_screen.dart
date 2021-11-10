import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';

final _logger = getLogger('training_log_screen');

class TrainingLogScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const TrainingLogScreen(this._day, this._workoutLog, {final Key? key = _key}) : super(key: key);

  static const Key _key = Key('training_log_screen');
  static const routeName = '/training-log';
  final DateTime? _day;
  final WorkoutLog? _workoutLog;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Scaffold(
      appBar: MainAppBar(currentlyViewedDate: _day),
      drawer: const AppDrawer(),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: const [
          _WorkoutFloatingActionButton(),
          SizedBox(height: 15),
        ],
      ),
      body: _day != null
          ? Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  Align(
                    alignment: Alignment.topLeft,
                    child: Text(
                      _day!.getStringRepresentation(
                        todayText: uiStrings.dateFormat_today,
                        yesterdayText: uiStrings.dateFormat_yesterday,
                      ),
                      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                    ),
                  ),
                  const SizedBox(height: 14),
                  Center(
                    child: _workoutLog != null ? const Text("placeholder") : Text(uiStrings.trainingLogScreen_placeholder),
                  ),
                ],
              ),
            )
          : Center(
              child: Text(uiStrings.trainingLogScreen_error_noDaySelected),
            ),
    );
  }
}

class _WorkoutFloatingActionButton extends StatefulWidget {
  const _WorkoutFloatingActionButton({final Key? key}) : super(key: key);

  @override
  _WorkoutFloatingActionButtonState createState() => _WorkoutFloatingActionButtonState();
}

class _WorkoutFloatingActionButtonState extends State<_WorkoutFloatingActionButton> with StringLocalizer {
  var _isOpened = false;

  set isOpened(final bool isOpened) {
    setState(() => _isOpened = isOpened);
  }

  bool get isOpened {
    return _isOpened;
  }

  SpeedDialChild _buildSpeedDialChild(final ThemeData theme, final IconData icon, final String label, final void Function() onTap) {
    return SpeedDialChild(
      backgroundColor: theme.colorScheme.secondary,
      foregroundColor: theme.colorScheme.onSecondary,
      child: Icon(icon),
      label: label,
      onTap: onTap,
    );
  }

  @override
  Widget build(final BuildContext context) {
    final theme = Theme.of(context);
    final uiStrings = getLocalizedStrings(context);
    return SpeedDial(
      backgroundColor: theme.colorScheme.secondary,
      foregroundColor: theme.colorScheme.onSecondary,
      renderOverlay: false,
      isOpenOnStart: false,
      onOpen: () => isOpened = true,
      onClose: () => isOpened = false,
      icon: isOpened ? Icons.close : Icons.add,
      spacing: 2,
      children: [
        _buildSpeedDialChild(theme, Icons.add, uiStrings.trainingLogScreen_speedDial_logNewWorkout, () {}),
        _buildSpeedDialChild(theme, Icons.content_paste, uiStrings.trainingLogScreen_speedDial_logWorkoutFromProgram, () {}),
        _buildSpeedDialChild(theme, Icons.copy, uiStrings.trainingLogScreen_speedDial_copyWorkout, () {}),
      ],
    );
  }
}
