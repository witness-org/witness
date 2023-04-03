import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/common/requester_state.dart';
import 'package:client/widgets/workouts/set_log_form_input.dart';
import 'package:client/widgets/workouts/set_logs_table.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ExerciseLogItemContent extends StatefulWidget {
  const ExerciseLogItemContent(this._workoutLog, this._exerciseLog, {final Key? key}) : super(key: key);

  final ExerciseLog _exerciseLog;
  final WorkoutLog? _workoutLog;

  @override
  State<ExerciseLogItemContent> createState() => _ExerciseLogItemContentState();
}

class _ExerciseLogItemContentState extends RequesterState<ExerciseLogItemContent, WorkoutLog> {
  late ExerciseLog _exerciseLog = widget._exerciseLog;

  Future<void> _updateSetLog(
    final BuildContext context,
    final WorkoutLog workoutLog,
    final WorkoutLogProvider provider,
    final SetLogFormInput formInput,
  ) async {
    final uiStrings = getLocalizedStrings(context);
    final setLog = _getSetLogFromFormInput(formInput);
    if (setLog != null) {
      submitRequestWithResponse(
        () => provider.putSetLog(workoutLog, setLog),
        defaultErrorMessage: uiStrings.exerciseLogItem_updateSetLogDefaultError,
        showProgressLoader: false,
      );
    }
  }

  Future<void> _deleteSetLog(
    final BuildContext context,
    final WorkoutLog workoutLog,
    final WorkoutLogProvider provider,
    final SetLog setLog,
  ) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.deleteSetLog(workoutLog, setLog),
      defaultErrorMessage: uiStrings.exerciseLogItem_deleteSetLogDefaultError,
      showProgressLoader: false,
    );
  }

  Future<void> _updateSetLogPositions(
    final BuildContext context,
    final WorkoutLog workoutLog,
    final WorkoutLogProvider provider,
    final Map<String, int> positions,
  ) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.putSetLogPositions(workoutLog, _exerciseLog, positions),
      defaultErrorMessage: uiStrings.exerciseLogItem_updateSetLogPositionsDefaultError,
      showProgressLoader: false,
    );
  }

  // override needed because otherwise, changes in the exercise log (e.g. due to setting a new exercise log comment) would not be passed on from the
  // widget to the state and hence, not displayed in the UI
  @override
  void didUpdateWidget(final ExerciseLogItemContent oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget._exerciseLog != oldWidget._exerciseLog) {
      _exerciseLog = widget._exerciseLog;
    }
  }

  @override
  Widget build(final BuildContext context) {
    final provider = Provider.of<WorkoutLogProvider>(context, listen: false);
    return Padding(
      padding: const EdgeInsets.only(left: 14, right: 14),
      child: widget._workoutLog != null
          ? SetLogsTable(
              _exerciseLog,
              (final context, final setLogFormInput) => _updateSetLog(context, widget._workoutLog!, provider, setLogFormInput),
              (final context, final setLog) => _deleteSetLog(context, widget._workoutLog!, provider, setLog),
              (final context, final positions) => _updateSetLogPositions(context, widget._workoutLog!, provider, positions),
            )
          : SetLogsTable(_exerciseLog, null, null, null),
    );
  }

  SetLog? _getSetLogFromFormInput(final SetLogFormInput formInput) {
    if (formInput.id == null || formInput.position == null) {
      return null;
    }

    switch (formInput.loggingType) {
      case LoggingType.reps:
        return RepsSetLog(
          id: formInput.id!,
          exerciseLogId: _exerciseLog.id,
          position: formInput.position!,
          reps: formInput.loggedValue,
          weightG: formInput.weightG,
          resistanceBands: formInput.resistanceBandList,
          rpe: formInput.rpe,
        );
      case LoggingType.time:
        return TimeSetLog(
          id: formInput.id!,
          exerciseLogId: _exerciseLog.id,
          position: formInput.position!,
          seconds: formInput.loggedValue,
          weightG: formInput.weightG,
          resistanceBands: formInput.resistanceBandList,
          rpe: formInput.rpe,
        );
      default:
        return null;
    }
  }
}
