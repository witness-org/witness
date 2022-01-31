import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/workout_log_create.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/error_key_translator.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:client/widgets/workouts/workout_log_item.dart';
import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:provider/provider.dart';
import 'package:timezone/timezone.dart';

final _logger = getLogger('workout_log_screen');

class WorkoutLogScreen extends StatefulWidget {
  const WorkoutLogScreen(this._date, {final Key? key = const Key('workout_log_screen')}) : super(key: key);

  static const routeName = '/workout-log';
  final TZDateTime _date;

  @override
  State<StatefulWidget> createState() => _WorkoutLogScreenState();
}

class _WorkoutLogScreenState extends State<WorkoutLogScreen> with LogMessagePreparer, StringLocalizer, ErrorKeyTranslator {
  String? _error;

  Future<void> _fetchWorkoutLogsByDate(final BuildContext context, final TZDateTime date) async {
    _logger.v(prepare('_fetchWorkoutLogsByDate()'));
    await Provider.of<WorkoutLogProvider>(context, listen: false).fetchWorkoutLogsByDate(date);
  }

  Future<void> _createNewWorkoutLog(final StringLocalizations uiStrings) async {
    _logger.v(prepare('_postNewWorkout()'));
    final response = await Provider.of<WorkoutLogProvider>(context, listen: false).postNewWorkoutLog(
      WorkoutLogCreate.empty(widget._date.onlyDate()),
    );

    if (response.isError) {
      setState(() => _error = translate(uiStrings, response.error!));
    }
  }

  Widget _buildHeader(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    return Container(
      margin: const EdgeInsets.only(bottom: 20.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
            child: Text(
              // TODO(raffaelfoidl): fix UI when text too long
              widget._date.getStringRepresentation(
                todayText: uiStrings.dateFormat_today,
                yesterdayText: uiStrings.dateFormat_yesterday,
              ),
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildWorkoutLogList(final BuildContext context, final StringLocalizations uiStrings, final TZDateTime date) {
    _logger.v(prepare('_buildWorkoutLogList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchWorkoutLogsByDate(context, date),
        builder: (final _, final snapshot) {
          return snapshot.waitSwitch(
            RefreshIndicator(
              onRefresh: () => _fetchWorkoutLogsByDate(context, date),
              child: Consumer<WorkoutLogProvider>(
                builder: (final _, final workoutLogData, final __) {
                  _logger.v(prepare('_buildWorkoutLogList.Consumer.builder()'));
                  final logs = workoutLogData.getWorkoutLogsByDay(date);
                  return logs.isEmpty
                      ? Center(
                          child: Text(
                            _error ?? uiStrings.workoutLogScreen_placeholder,
                            textAlign: TextAlign.center,
                          ),
                        )
                      : Scrollbar(
                          child: ListView.builder(
                            itemCount: logs.length,
                            itemBuilder: (final _, final index) {
                              final log = logs[index];
                              return Padding(
                                padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
                                child: Column(
                                  children: [
                                    WorkoutLogItem(index, log),
                                    const Divider(),
                                  ],
                                ),
                              );
                            },
                          ),
                        );
                },
              ),
            ),
            errorWidget: (final error) {
              return Center(
                child: Text(
                  uiStrings.workoutLogScreen_workoutLogList_errorMessage(translate(uiStrings, error.toString())),
                  textAlign: TextAlign.center,
                ),
              );
            },
          );
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
        children: [
          _WorkoutFloatingActionButton(_createNewWorkoutLog),
          const SizedBox(height: 15),
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(context, uiStrings, date),
          _buildWorkoutLogList(context, uiStrings, date),
        ],
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return _buildScreen(context, uiStrings, widget._date);
  }
}

class _WorkoutFloatingActionButton extends StatefulWidget {
  const _WorkoutFloatingActionButton(this._createNewWorkoutAction, {final Key? key}) : super(key: key);

  final void Function(StringLocalizations uiStrings) _createNewWorkoutAction;

  @override
  _WorkoutFloatingActionButtonState createState() => _WorkoutFloatingActionButtonState();
}

class _WorkoutFloatingActionButtonState extends State<_WorkoutFloatingActionButton> with StringLocalizer {
  var _isOpened = false;

  set isOpened(final bool isOpened) {
    setState(() => _isOpened = isOpened);
  }

  bool get isOpened => _isOpened;

  SpeedDialChild _buildSpeedDialChild(final ThemeData theme, final IconData icon, final String label, final void Function()? onTap) {
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
        _buildSpeedDialChild(theme, Icons.add, uiStrings.workoutLogScreen_speedDial_logNewWorkout, () => widget._createNewWorkoutAction(uiStrings)),
        _buildSpeedDialChild(theme, Icons.content_paste, uiStrings.workoutLogScreen_speedDial_logWorkoutFromProgram, () {}),
        _buildSpeedDialChild(theme, Icons.copy, uiStrings.workoutLogScreen_speedDial_copyWorkout, () {}),
      ],
    );
  }
}
