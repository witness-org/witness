import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:provider/provider.dart';
import 'package:timezone/timezone.dart';

final _logger = getLogger('training_log_screen');

class TrainingLogScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const TrainingLogScreen(this._date, {final Key? key = _key}) : super(key: key);

  static const Key _key = Key('training_log_screen');
  static const routeName = '/training-log';
  final TZDateTime _date;

  Future<void> _fetchWorkoutLogsByDate(final BuildContext context, final TZDateTime date) async {
    _logger.v(prepare('_fetchWorkoutLogsByDate()'));
    await Provider.of<WorkoutLogProvider>(context, listen: false).fetchWorkoutLogsByDate(date);
  }

  Widget _buildHeader(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          // TODO(raffaelfoidl): fix UI when text too long
          _date.getStringRepresentation(
            todayText: uiStrings.dateFormat_today,
            yesterdayText: uiStrings.dateFormat_yesterday,
          ),
          style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        const Spacer(),
      ],
    );
  }

  Widget _buildWorkoutLogList(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    _logger.v(prepare('_buildWorkoutLogList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchWorkoutLogsByDate(context, date),
        builder: (final _, final snapshot) {
          if (snapshot.hasError) {
            return Center(
              child: Text(
                'Could not fetch workout logs:\n ' + snapshot.error.toString(),
                textAlign: TextAlign.center,
              ),
            );
          } else {
            return snapshot.waitSwitch(
              RefreshIndicator(
                onRefresh: () => _fetchWorkoutLogsByDate(context, date),
                child: Consumer<WorkoutLogProvider>(
                  builder: (final _, final workoutLogData, final __) {
                    _logger.v(prepare('_buildWorkoutLogList.Consumer.builder()'));
                    final logs = workoutLogData.getWorkoutLogsByDate(date);
                    return logs.isEmpty
                        ? Center(
                            child: Text(uiStrings.trainingLogScreen_placeholder),
                          )
                        : Scrollbar(
                            isAlwaysShown: true,
                            child: ListView.builder(
                              itemCount: logs.length,
                              itemBuilder: (final _, final index) {
                                final log = logs[index];
                                return Column(
                                  children: [
                                    _WorkoutLogItem(index, log),
                                    const Divider(),
                                  ],
                                );
                              },
                            ),
                          );
                  },
                ),
              ),
            );
          }
        },
      ),
    );
  }

  Widget _buildScreen(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: MainAppBar(currentlyViewedDate: date),
      drawer: const AppDrawer(),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: const [
          _WorkoutFloatingActionButton(),
          SizedBox(height: 15),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeader(context, uiStrings, date),
            _buildWorkoutLogList(context, uiStrings, date),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return _buildScreen(context, uiStrings, _date);
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

class _WorkoutLogItem extends StatelessWidget with LogMessagePreparer {
  const _WorkoutLogItem(this._index, this._workoutLog, {final Key? key}) : super(key: key);
  final int _index;
  final WorkoutLog _workoutLog;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return Card(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'Workout ${_index + 1}',
                style: const TextStyle(
                  fontSize: 16,
                ),
              ),
              Text(
                'Duration: ${_workoutLog.durationMinutes} min.',
                style: const TextStyle(
                  fontSize: 16,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}
