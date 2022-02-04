import 'package:client/extensions/map_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/workouts/select_workout_log_dialog.dart';
import 'package:flutter/material.dart';
import 'dart:math' as math;

final _logger = getLogger('copy_exercise_logs_dialog');

class CopyExerciseLogsDialog extends StatefulWidget {
  const CopyExerciseLogsDialog(
    this._workoutLogsToCopyFrom,
    this._existingWorkoutLogs,
    this._copyExerciseLogsToExistingWorkoutLog,
    this._createNewWorkoutLogWithCopiedExerciseLogs, {
    final Key? key,
  }) : super(key: key);

  final List<WorkoutLog> _workoutLogsToCopyFrom;
  final List<WorkoutLog> _existingWorkoutLogs;
  final void Function(WorkoutLog workoutLog, List<ExerciseLog> exerciseLogsToCopy) _copyExerciseLogsToExistingWorkoutLog;
  final void Function(List<ExerciseLog> exerciseLogsToCopy) _createNewWorkoutLogWithCopiedExerciseLogs;

  @override
  State<StatefulWidget> createState() => _CopyExerciseLogsDialogState();
}

class _CopyExerciseLogsDialogState extends State<CopyExerciseLogsDialog> with StringLocalizer, LogMessagePreparer {
  List<ExerciseLog> _exerciseLogs = [];
  Map<int, Map<int, bool>> _exerciseLogsToCopy = {};

  Future<bool> _addExerciseLogsToExistingWorkoutLog(final List<ExerciseLog> exerciseLogs) async {
    var closeDialog = true;

    if (widget._existingWorkoutLogs.length == 1) {
      // only one existing workout -> add to this one
      widget._copyExerciseLogsToExistingWorkoutLog(widget._existingWorkoutLogs[0], exerciseLogs);
    } else {
      // more than one existing workout -> choose to which one to add exercise logs
      closeDialog = await _showWorkoutLogSelectionDialog(exerciseLogs) ?? false;
    }

    return closeDialog;
  }

  Future<bool?> _showWorkoutLogSelectionDialog(final List<ExerciseLog> exerciseLogsToCopy) async {
    return showDialog<bool>(
      context: context,
      builder: (final BuildContext context) => SelectWorkoutLogDialog(
        widget._existingWorkoutLogs,
        (final workoutLog) => widget._copyExerciseLogsToExistingWorkoutLog(workoutLog, exerciseLogsToCopy),
      ),
    );
  }

  Widget _buildExerciseLogChecklist(final StringLocalizations uiStrings, final int workoutLogId, final List<ExerciseLog> exerciseLogs) {
    return ListView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: exerciseLogs.length,
      itemBuilder: (final _, final index) {
        final exerciseLog = exerciseLogs[index];
        return CheckboxListTile(
          value: _exerciseLogsToCopy[workoutLogId]?[exerciseLog.id] ?? false,
          onChanged: (final checked) => setState(() {
            _exerciseLogsToCopy[workoutLogId]?[exerciseLog.id] = checked ?? false;
          }),
          title: Text(exerciseLog.exercise.name),
          subtitle: Text(
            uiStrings.workoutLogScreen_copyExerciseLogsDialog_setNumberIndicator(exerciseLog.setLogs.length),
          ),
        );
      },
    );
  }

  Widget _buildWorkoutLogList(final StringLocalizations uiStrings, final List<WorkoutLog> workoutLogs) {
    return SizedBox(
      width: 330.0,
      height: math.min(workoutLogs.length * 15 + _exerciseLogs.length * 45 + 90, 440),
      child: Scrollbar(
        child: ListView.builder(
          shrinkWrap: true,
          itemCount: workoutLogs.length,
          itemBuilder: (final _, final index) {
            final workoutLog = workoutLogs[index];
            return Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      uiStrings.workoutLogScreen_copyExerciseLogsDialog_workoutLogHeading(index + 1),
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    Switch(
                      value: _exerciseLogsToCopy[workoutLog.id]?.values.every((final checked) => checked) ?? false,
                      onChanged: (final checked) => setState(() {
                        if (_exerciseLogsToCopy.containsKey(workoutLog.id)) {
                          _exerciseLogsToCopy[workoutLog.id]!.updateAll((final key, final value) => checked);
                        }
                      }),
                    )
                  ],
                ),
                _buildExerciseLogChecklist(uiStrings, workoutLog.id, workoutLog.exerciseLogs),
              ],
            );
          },
        ),
      ),
    );
  }

  List<Widget> _buildActionButtons(final StringLocalizations uiStrings) {
    return [
      TextButton(
        onPressed: () => Navigator.pop(context),
        child: Text(uiStrings.workoutLogScreen_copyExerciseLogsDialog_cancel),
      ),
      if (widget._existingWorkoutLogs.isNotEmpty)
        TextButton(
          onPressed: _isAtLeastOneExerciseLogSelected(_exerciseLogsToCopy)
              ? () {
                  final exerciseLogsToCopy = _getExerciseLogsToCopy();
                  _addExerciseLogsToExistingWorkoutLog(exerciseLogsToCopy).then((final closeDialog) {
                    if (closeDialog) {
                      Navigator.pop(context);
                    }
                  });
                }
              : null,
          child: Text(uiStrings.workoutLogScreen_copyExerciseLogsDialog_addToExistingWorkout),
        ),
      TextButton(
        onPressed: _isAtLeastOneExerciseLogSelected(_exerciseLogsToCopy)
            ? () => widget._createNewWorkoutLogWithCopiedExerciseLogs(_getExerciseLogsToCopy())
            : null,
        child: Text(uiStrings.workoutLogScreen_copyExerciseLogsDialog_createNewWorkout),
      ),
    ];
  }

  @override
  void initState() {
    super.initState();

    _exerciseLogs = _getAllExerciseLogsFromWorkoutLogs(widget._workoutLogsToCopyFrom);
    _exerciseLogsToCopy = {
      for (final workoutLog in widget._workoutLogsToCopyFrom) workoutLog.id: {for (final exerciseLog in workoutLog.exerciseLogs) exerciseLog.id: true}
    };
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return AlertDialog(
      scrollable: true,
      title: Text(uiStrings.workoutLogScreen_copyExerciseLogsDialog_title),
      content: _buildWorkoutLogList(uiStrings, widget._workoutLogsToCopyFrom),
      actions: _buildActionButtons(uiStrings),
    );
  }

  List<ExerciseLog> _getExerciseLogsToCopy() {
    final exerciseLogsToCopyWithoutWorkoutLogId = _getExerciseLogsToCopyWithoutWorkoutLogId(_exerciseLogsToCopy);
    return _exerciseLogs.where((final exerciseLog) => exerciseLogsToCopyWithoutWorkoutLogId[exerciseLog.id] ?? false).toList();
  }

  /// Gets a list of lists of exercise logs from all workout logs provided by `workoutLogs` and flattens them into one single list which is returned.
  static List<ExerciseLog> _getAllExerciseLogsFromWorkoutLogs(final List<WorkoutLog> workoutLogs) {
    return workoutLogs.map((final workoutLog) => workoutLog.exerciseLogs).expand((final exerciseLogs) => exerciseLogs).toList();
  }

  /// Checks whether at least one exercise log from any workout log is selected (i.e. should be copied).
  static bool _isAtLeastOneExerciseLogSelected(final Map<int, Map<int, bool>> exerciseLogsToCopy) {
    return exerciseLogsToCopy.values.any((final exerciseLogs) => exerciseLogs.holdsForAny((final entry) => entry.value));
  }

  /// "Flattens" the map of workout log IDs to maps of IDs of exercise logs contained in the respective workout log to a [bool] indicating whether the
  /// respective exercise log should be copied to a map of exercise log IDs to a [bool] indicator.
  static Map<int, bool> _getExerciseLogsToCopyWithoutWorkoutLogId(final Map<int, Map<int, bool>> exerciseLogsToCopyWithWorkoutLogId) {
    return exerciseLogsToCopyWithWorkoutLogId.values.reduce((final value, final element) {
      value.addAll(element);
      return value;
    });
  }
}
