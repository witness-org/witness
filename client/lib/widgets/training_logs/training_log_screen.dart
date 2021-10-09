import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:client/widgets/training_logs/api_consumer_showcase.dart';
import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';

final _logger = getLogger('training_log_screen');

class TrainingLogScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const TrainingLogScreen(this._workoutDay, {final Key? key}) : super(key: key);

  static const routeName = '/training-log';
  final DateTime _workoutDay;

  void _selectDate(final BuildContext context) {
    final referenceDate = _workoutDay;
    showDatePicker(
      context: context,
      initialEntryMode: DatePickerEntryMode.calendarOnly,
      initialDate: referenceDate,
      firstDate: referenceDate.subtractYears(1),
      lastDate: referenceDate.addYears(1),
    ).then((final pickedDate) {
      if (pickedDate != null) {
        Navigator.of(context).pushReplacementNamed(TrainingLogScreen.routeName, arguments: pickedDate.dateOnly());
      }
    });
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Scaffold(
      appBar: MainAppBar(currentlyViewedDate: _workoutDay.dateOnly()),
      drawer: const AppDrawer(),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          const _WorkoutFloatingActionButton(),
          const SizedBox(height: 15),
          FloatingActionButton(
            tooltip: uiStrings.trainingLogScreen_action_selectDay,
            child: const Icon(Icons.calendar_today),
            onPressed: () => _selectDate(context),
          ),
        ],
      ),
      body: Column(
        children: [
          Center(
            child: Text('${uiStrings.trainingLogScreen_placeholder_prefix} $_workoutDay'),
          ),
          const ApiConsumerShowcase(),
        ],
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
        _buildSpeedDialChild(theme, Icons.fitness_center, uiStrings.trainingLogScreen_speedDial_logExercise, () {}),
        _buildSpeedDialChild(theme, Icons.copy, uiStrings.trainingLogScreen_speedDial_logWorkout, () {}),
        _buildSpeedDialChild(theme, Icons.content_paste, uiStrings.trainingLogScreen_speedDial_copyWorkout, () {}),
      ],
    );
  }
}
