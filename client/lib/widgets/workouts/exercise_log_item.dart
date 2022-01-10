import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/reps_set_log_create.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/set_log_create.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/models/workouts/time_set_log_create.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/common/requester_state.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/exercise_log_comment_dialog.dart';
import 'package:client/widgets/workouts/set_log_dialog.dart';
import 'package:client/widgets/workouts/set_log_form_input.dart';
import 'package:client/widgets/workouts/set_logs_table.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercise_log_item');

class ExerciseLogItem extends StatefulWidget {
  const ExerciseLogItem(this._workoutLog, this._exerciseLog, {final Key? key}) : super(key: key);

  final WorkoutLog _workoutLog;
  final ExerciseLog _exerciseLog;

  @override
  State<StatefulWidget> createState() => _ExerciseLogItemState();
}

class _ExerciseLogItemState extends RequesterState<ExerciseLogItem, WorkoutLog> with StringLocalizer, LogMessagePreparer {
  late ExerciseLog _exerciseLog = widget._exerciseLog;
  bool _showSetLogs = false;

  Future<void> _deleteExerciseLog(final BuildContext context, final WorkoutLogProvider provider) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.deleteExerciseLog(widget._workoutLog, _exerciseLog),
      defaultErrorMessage: uiStrings.exerciseLogItem_deleteExerciseLogDefaultError,
      showProgressLoader: false,
    );
  }

  Future<void> _addSetLog(final BuildContext context, final WorkoutLogProvider provider, final SetLogFormInput formInput) async {
    final uiStrings = getLocalizedStrings(context);
    final setLogCreate = _getSetLogCreateFromFormInput(formInput);
    if (setLogCreate != null) {
      submitRequestWithResponse(
        () => provider.postNewSetLog(widget._workoutLog, _exerciseLog, setLogCreate),
        defaultErrorMessage: uiStrings.exerciseLogItem_addNewSetLogDefaultError,
        showProgressLoader: false,
      );
    }
  }

  Future<void> _updateExerciseLogComment(final BuildContext context, final WorkoutLogProvider provider, final String? comment) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.patchExerciseLogComment(widget._workoutLog, _exerciseLog, comment),
      defaultErrorMessage: uiStrings.exerciseLogItem_setCommentDefaultError,
      showProgressLoader: false,
    );
  }

  Future<void> _updateSetLog(final BuildContext context, final WorkoutLogProvider provider, final SetLogFormInput formInput) async {
    final uiStrings = getLocalizedStrings(context);
    final setLog = _getSetLogFromFormInput(formInput);
    if (setLog != null) {
      submitRequestWithResponse(
        () => provider.putSetLog(widget._workoutLog, setLog),
        defaultErrorMessage: uiStrings.exerciseLogItem_updateSetLogDefaultError,
        showProgressLoader: false,
      );
    }
  }

  Future<void> _deleteSetLog(final BuildContext context, final WorkoutLogProvider provider, final SetLog setLog) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.deleteSetLog(widget._workoutLog, setLog),
      defaultErrorMessage: uiStrings.exerciseLogItem_deleteSetLogDefaultError,
      showProgressLoader: false,
    );
  }

  Future<void> _updateSetLogPositions(final BuildContext context, final WorkoutLogProvider provider, final Map<String, int> positions) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.putSetLogPositions(widget._workoutLog, _exerciseLog, positions),
      defaultErrorMessage: uiStrings.exerciseLogItem_updateSetLogPositionsDefaultError,
      showProgressLoader: false,
    );
  }

  void _openSetLogDialog(final BuildContext context, final WorkoutLogProvider provider, {final SetLog? setLog}) {
    _showDialog(
      context,
      SetLogDialog(_exerciseLog, setLog, (final _context, final _setLogFormInput) => _addSetLog(_context, provider, _setLogFormInput)),
    );
  }

  void _openCommentDialog(final BuildContext context, final WorkoutLogProvider provider) {
    _showDialog(
      context,
      ExerciseLogCommentDialog(
        _exerciseLog.comment,
        (final _context, final _updatedComment) => _updateExerciseLogComment(_context, provider, _updatedComment),
      ),
    );
  }

  void _showDialog(final BuildContext context, final Widget widget) {
    showDialog<String>(
      context: context,
      builder: (final BuildContext context) => widget,
    );
  }

  Widget? _buildExerciseCardSubtitle(final ExerciseLog exerciseLog, final void Function() onCommentPressed) {
    return exerciseLog.comment != null
        ? TextButton(
            child: Row(
              children: [
                const Icon(Icons.notes_outlined, size: 15),
                const SizedBox(width: 5),
                Flexible(
                  child: Text(
                    exerciseLog.comment!,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
            style: TextButton.styleFrom(
              primary: Colors.grey,
              tapTargetSize: MaterialTapTargetSize.shrinkWrap,
            ),
            onPressed: onCommentPressed,
          )
        : null;
  }

  Widget _buildSetLogList(final WorkoutLogProvider provider) {
    return Padding(
      padding: const EdgeInsets.only(left: 14, right: 14),
      child: SetLogsTable(
        _exerciseLog,
        (final _context, final _setLogFormInput) => _updateSetLog(_context, provider, _setLogFormInput),
        (final _context, final _setLog) => _deleteSetLog(_context, provider, _setLog),
        (final _context, final _positions) => _updateSetLogPositions(_context, provider, _positions),
      ),
    );
  }

  List<Widget> _buildButton(final String text, final void Function() onPressed, {final ButtonStyle? buttonStyle}) {
    return [
      TextButton(
        style: buttonStyle,
        child: Text(text),
        onPressed: onPressed,
      ),
      const SizedBox(width: 8),
    ];
  }

  Widget _buildButtonRow(
    final BuildContext context,
    final WorkoutLogProvider provider,
    final bool addCommentButton,
    final StringLocalizations uiStrings,
  ) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        ..._buildButton(
          uiStrings.exerciseLogItem_delete,
          () => _deleteExerciseLog(context, provider),
          buttonStyle: TextButton.styleFrom(primary: Theme.of(context).errorColor),
        ),
        ..._buildButton(uiStrings.exerciseLogItem_addSet, () => _openSetLogDialog(context, provider)),
        if (addCommentButton) ..._buildButton(uiStrings.exerciseLogItem_addComment, () => _openCommentDialog(context, provider)),
      ],
    );
  }

  // override needed because otherwise, changes in the exercise log (e.g. due to setting a new exercise log comment) would not be passed on from the
  // widget to the state and hence, not displayed in the UI
  @override
  void didUpdateWidget(final ExerciseLogItem oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget._exerciseLog != oldWidget._exerciseLog) {
      _exerciseLog = widget._exerciseLog;
    }
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    final provider = Provider.of<WorkoutLogProvider>(context, listen: false);
    return Card(
      child: ExpansionTile(
        title: Text(_exerciseLog.exercise.name),
        subtitle: _buildExerciseCardSubtitle(_exerciseLog, () => _openCommentDialog(context, provider)),
        children: [
          _buildSetLogList(provider),
          _buildButtonRow(context, provider, _exerciseLog.comment == null, uiStrings),
        ],
        initiallyExpanded: _showSetLogs,
        onExpansionChanged: (final _expanded) => setState(() => _showSetLogs = _expanded),
        maintainState: true,
      ),
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

  static SetLogCreate? _getSetLogCreateFromFormInput(final SetLogFormInput formInput) {
    switch (formInput.loggingType) {
      case LoggingType.reps:
        return RepsSetLogCreate(
          reps: formInput.loggedValue,
          weightG: formInput.weightG,
          resistanceBands: formInput.resistanceBandList,
          rpe: formInput.rpe,
        );
      case LoggingType.time:
        return TimeSetLogCreate(
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