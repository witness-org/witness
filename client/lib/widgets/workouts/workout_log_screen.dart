import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/exercise_log_create.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/models/workouts/workout_log_create.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/requester_state.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/common/error_key_translator.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:client/widgets/workouts/copy_exercise_logs_dialog.dart';
import 'package:client/widgets/workouts/workout_log_list.dart';
import 'package:flutter/material.dart';
import 'package:flutter_speed_dial/flutter_speed_dial.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
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

class _WorkoutLogScreenState extends RequesterState<WorkoutLogScreen, WorkoutLog> with LogMessagePreparer, StringLocalizer, ErrorKeyTranslator {
  String? _error;

  Future<void> _fetchLoggingDaysInMonth(
    final WorkoutLogProvider provider,
    final TZDateTime date,
    final void Function(Object error, StackTrace stackTrace) errorAction,
  ) async {
    _logger.v(prepare('_fetchLoggingDaysInMonth()'));
    final startDate = TZDateTime.local(date.year, date.month, 1); // first day of month
    final endDate = TZDateTime.local(date.year, date.month + 1, 0); // last day of month
    provider.getLoggingDaysInPeriod(startDate, endDate).onError(errorAction);
  }

  Future<void> _createNewWorkoutLog(final StringLocalizations uiStrings, final WorkoutLogProvider provider, final WorkoutLogCreate workoutLog) async {
    _logger.v(prepare('_createNewWorkout()'));
    submitRequestWithResponse(
      () => provider.postNewWorkoutLog(workoutLog),
      showProgressLoader: false,
    );
  }

  Future<void> _createNewExerciseLogsForWorkoutLog(
    final StringLocalizations uiStrings,
    final WorkoutLogProvider provider,
    final WorkoutLog workoutLog,
    final List<ExerciseLog> exerciseLogs,
  ) async {
    _logger.v(prepare('_createNewExerciseLogsForWorkout()'));
    submitRequestWithResponse(
      () => provider.postNewExerciseLogs(workoutLog, exerciseLogs.map(ExerciseLogCreate.fromExerciseLog).toList()),
      showProgressLoader: false,
    );
  }

  void _removeError() {
    setState(() => _error = null);
  }

  void _setError(final StringLocalizations uiStrings, final String error) {
    setState(() => _error = translate(uiStrings, error));
  }

  Future<void> _showCopyWorkoutLogsDialog(final StringLocalizations uiStrings, final WorkoutLogProvider provider, final TZDateTime date) async {
    await ProgressLoader().show(context);
    provider.fetchWorkoutLogsByDay(date).then((final _) {
      final workoutLogs = provider.getWorkoutLogsByDay(date).where((final workoutLog) => workoutLog.exerciseLogs.isNotEmpty).toList();

      if (workoutLogs.isNotEmpty) {
        showDialog<void>(
          context: context,
          builder: (final BuildContext context) {
            return CopyExerciseLogsDialog(
              workoutLogs,
              provider.getWorkoutLogsByDay(widget._date),
              (final workoutLog, final exerciseLogs) => _createNewExerciseLogsForWorkoutLog(uiStrings, provider, workoutLog, exerciseLogs),
              (final exerciseLogs) {
                final workoutLog = WorkoutLogCreate(
                  loggedOn: widget._date.onlyDate(),
                  exerciseLogs: exerciseLogs.map(ExerciseLogCreate.fromExerciseLog).toList(),
                );

                _createNewWorkoutLog(uiStrings, provider, workoutLog);
                Navigator.pop(context);
              },
            );
          },
        );
      }
    }).onError((final Object error, final StackTrace stackTrace) {
      showError(translate(uiStrings, error.toString()));
    });

    await ProgressLoader().dismiss();
  }

  bool _copyExerciseLogsFromDay(
    final StringLocalizations uiStrings,
    final WorkoutLogProvider provider,
    final DateTime selectedDate,
    final TZDateTime referenceDate,
  ) {
    final zonedDate = selectedDate.onlyTZDate();

    if (!zonedDate.isAtSameMomentAs(referenceDate.onlyDate()) && provider.hasWorkoutLogsWithExerciseLogs(zonedDate)) {
      _showCopyWorkoutLogsDialog(uiStrings, provider, zonedDate);
      return true;
    }

    return false;
  }

  Widget _buildFloatingActionButton(final StringLocalizations uiStrings, final WorkoutLogProvider provider, final TZDateTime date) {
    return _WorkoutFloatingActionButton(
      () => _createNewWorkoutLog(uiStrings, provider, WorkoutLogCreate.empty(widget._date.onlyDate())),
      date,
      (final innerProvider, final selectedDate) {
        final closeDatePicker = _copyExerciseLogsFromDay(uiStrings, innerProvider, selectedDate, date);

        if (closeDatePicker) {
          Navigator.pop(context);
        }
      },
      (final innerDate) async => _fetchLoggingDaysInMonth(
        provider,
        innerDate.onlyTZDate(),
        (final error, final _) {
          showError(translate(uiStrings, error.toString())); // display snack bar with error message
          Navigator.pop(context); // close DatePicker
        },
      ),
      (final innerProvider, final innerDate) => innerProvider.hasWorkoutLogsWithExerciseLogs(innerDate.onlyTZDate()),
      (final innerDate) => _fetchLoggingDaysInMonth(
        provider,
        innerDate,
        (final error, final _) => _setError(uiStrings, error.toString()),
      ),
    );
  }

  Widget _buildScreen(final StringLocalizations uiStrings, final WorkoutLogProvider provider, final TZDateTime date) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: MainAppBar(currentlyViewedDate: date),
      drawer: const AppDrawer(),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          _buildFloatingActionButton(uiStrings, provider, date),
          const SizedBox(height: 15),
        ],
      ),
      body: WorkoutLogList(
        date,
        _error,
        _removeError,
        (final error) => _setError(uiStrings, error),
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    final provider = Provider.of<WorkoutLogProvider>(context, listen: false);
    return _buildScreen(uiStrings, provider, widget._date);
  }
}

class _WorkoutFloatingActionButton extends StatefulWidget {
  const _WorkoutFloatingActionButton(
    this._createNewWorkoutAction,
    this._date,
    this._selectDateAction,
    this._onDisplayedMonthChangedAction,
    this._selectableDayPredicate,
    this._fetchLoggingDaysInMonth, {
    final Key? key,
  }) : super(key: key);

  final void Function() _createNewWorkoutAction;
  final TZDateTime _date;
  final void Function(WorkoutLogProvider provider, DateTime displayedDate) _selectDateAction;
  final void Function(DateTime displayedDate) _onDisplayedMonthChangedAction;
  final bool Function(WorkoutLogProvider provider, DateTime selectableDay) _selectableDayPredicate;
  final Future<void> Function(TZDateTime date) _fetchLoggingDaysInMonth;

  @override
  _WorkoutFloatingActionButtonState createState() => _WorkoutFloatingActionButtonState();
}

class _WorkoutFloatingActionButtonState extends State<_WorkoutFloatingActionButton> with StringLocalizer {
  var _isOpened = false;
  Future<void>? _fetchLoggingDaysInMonthResult;

  set isOpened(final bool isOpened) {
    setState(() => _isOpened = isOpened);
  }

  bool get isOpened => _isOpened;

  void _showDatePickerDialog(final String title) {
    showDialog<void>(
      context: context,
      builder: (final BuildContext context) {
        return FutureBuilder<void>(
          future: _fetchLoggingDaysInMonthResult,
          builder: (final _, final snapshot) {
            return Consumer<WorkoutLogProvider>(
              builder: (final _, final workoutLogData, final __) {
                return DialogHelper.getDatePicker(
                  context,
                  title,
                  widget._date,
                  (final date) => widget._selectDateAction(workoutLogData, date),
                  onDisplayedMonthChangedAction: widget._onDisplayedMonthChangedAction,
                  selectableDayPredicate: (final date) => widget._selectableDayPredicate(workoutLogData, date),
                );
              },
            );
          },
        );
      },
    );
  }

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
  void initState() {
    super.initState();
    _fetchLoggingDaysInMonthResult = widget._fetchLoggingDaysInMonth(widget._date);
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
        _buildSpeedDialChild(theme, Icons.add, uiStrings.workoutLogScreen_speedDial_logNewWorkout, widget._createNewWorkoutAction),
        _buildSpeedDialChild(theme, Icons.content_paste, uiStrings.workoutLogScreen_speedDial_logWorkoutFromProgram, () {}),
        _buildSpeedDialChild(
          theme,
          Icons.copy,
          uiStrings.workoutLogScreen_speedDial_copyFromWorkout,
          () => _showDatePickerDialog(uiStrings.dialogHelper_datePickerDialog_defaultTitle),
        ),
      ],
    );
  }
}
