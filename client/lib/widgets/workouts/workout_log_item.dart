import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/exercise_log_create.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/common/requester_state.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/workouts/exercise_log_item.dart';
import 'package:client/widgets/workouts/workout_log_duration_dialog.dart';
import 'package:client/widgets/workouts/workout_log_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('workout_log_item');

class WorkoutLogItem extends StatefulWidget {
  const WorkoutLogItem(this._index, this._workoutLog, {final Key? key}) : super(key: key);

  final int _index;
  final WorkoutLog _workoutLog;

  @override
  State<StatefulWidget> createState() => _WorkoutLogItemState();
}

class _WorkoutLogItemState extends RequesterState<WorkoutLogItem, WorkoutLog> with StringLocalizer, LogMessagePreparer {
  late WorkoutLog _workoutLog = widget._workoutLog;

  Future<void> _deleteWorkoutLog(final BuildContext context, final WorkoutLogProvider provider) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithoutResponse(
      () => provider.deleteWorkoutLog(_workoutLog),
      defaultErrorMessage: uiStrings.workoutLogItem_deleteWorkoutLogDefaultError,
      showProgressLoader: false,
    );
  }

  Future<void> _addExerciseLog(final BuildContext context, final WorkoutLogProvider provider, final Exercise exercise) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.postNewExerciseLog(_workoutLog, ExerciseLogCreate.empty(exercise.id)),
      defaultErrorMessage: uiStrings.workoutLogItem_addExerciseDefaultError,
      showProgressLoader: false,
    );
    // after exercise is selected, navigate back to workout log screen which is either the screen for a given date or the "home" screen
    Navigator.of(context).popUntil((final Route<dynamic> route) {
      return !route.willHandlePopInternally &&
          route is ModalRoute &&
          (route.settings.name == WorkoutLogScreen.routeName || route.settings.name == '/');
    });
  }

  Future<void> _updateWorkoutDuration(final BuildContext context, final WorkoutLogProvider provider, final int? durationMinutes) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.patchWorkoutLogDuration(_workoutLog, durationMinutes),
      defaultErrorMessage: uiStrings.workoutLogItem_workoutDurationDialog_durationDefaultError,
      showProgressLoader: false,
    );
  }

  Future<void> _updateExerciseLogPositions(final BuildContext context, final WorkoutLogProvider provider, final Map<String, int> positions) async {
    final uiStrings = getLocalizedStrings(context);
    submitRequestWithResponse(
      () => provider.putExerciseLogPositions(_workoutLog, positions),
      defaultErrorMessage: uiStrings.workoutLogItem_updateExerciseLogPositionsDefaultError,
      showProgressLoader: false,
    );
  }

  void _reorderExerciseLogs(final WorkoutLogProvider provider, final int oldIndex, final int newIndex) {
    setState(() {
      // this `setState()` call prevents the exercise log cards from "jumping around" due to them returning to their original positions before going
      // to their new positions when the server request is successfully completed
      final index = newIndex > oldIndex ? newIndex - 1 : newIndex;
      final item = _workoutLog.exerciseLogs.removeAt(oldIndex);
      _workoutLog.exerciseLogs.insert(index, item);
    });

    _updateExerciseLogPositions(context, provider, _createPositionsMap(_workoutLog.exerciseLogs));
  }

  Widget _buildWorkoutHeader(final String heading) {
    return Text(
      heading,
      style: const TextStyle(
        fontWeight: FontWeight.bold,
        fontSize: 16,
      ),
    );
  }

  Widget _buildWorkoutDurationButton(final String buttonText, final Function() onPressed) {
    return TextButton.icon(
      icon: const Icon(Icons.timer),
      label: Text(
        buttonText,
        style: const TextStyle(
          fontSize: 16,
        ),
      ),
      onPressed: onPressed,
    );
  }

  Widget _buildExerciseLogList(final WorkoutLogProvider provider, final List<ExerciseLog> exerciseLogs) {
    return ReorderableListView(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      onReorder: (final _oldIndex, final _newIndex) => _reorderExerciseLogs(provider, _oldIndex, _newIndex),
      children: exerciseLogs.map((final exerciseLog) {
        return ExerciseLogItem(
          _workoutLog,
          exerciseLog,
          key: ValueKey(exerciseLog.id),
        );
      }).toList(),
    );
  }

  Widget _buildWorkoutLogButtonRow(final BuildContext context, final WorkoutLogProvider provider) {
    final uiStrings = getLocalizedStrings(context);
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        TextButton(
          style: TextButton.styleFrom(primary: Theme.of(context).errorColor),
          child: Text(uiStrings.workoutLogItem_delete),
          onPressed: () {
            _deleteWorkoutLog(context, provider);
          },
        ),
        TextButton(
          child: Text(uiStrings.workoutLogItem_addExercise),
          onPressed: () => Navigator.of(context).pushNamed(
            ExercisesScreen.routeName,
            arguments: (final BuildContext _context, final Exercise _selectedExercise) => _addExerciseLog(_context, provider, _selectedExercise),
          ),
        ),
      ],
    );
  }

  // override needed because otherwise, changes in the workout log (e.g. due to setting a new value for the workout duration) would not be passed on
  // from the widget to the state and hence, not displayed in the UI
  @override
  void didUpdateWidget(final WorkoutLogItem oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget._workoutLog != oldWidget._workoutLog) {
      _workoutLog = widget._workoutLog;
    }
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    final provider = Provider.of<WorkoutLogProvider>(context, listen: false);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            _buildWorkoutHeader(
              uiStrings.workoutLogItem_workoutLog_heading(widget._index + 1),
            ),
            _buildWorkoutDurationButton(
              uiStrings.workoutLogItem_workoutLog_duration(_workoutLog.durationMinutes),
              () => showDialog<String>(
                context: context,
                builder: (final BuildContext context) => WorkoutLogDurationDialog(
                  _workoutLog.durationMinutes,
                  (final _context, final _updatedDuration) => _updateWorkoutDuration(_context, provider, _updatedDuration),
                ),
              ),
            ),
          ],
        ),
        _buildExerciseLogList(provider, _workoutLog.exerciseLogs),
        _buildWorkoutLogButtonRow(context, provider)
      ],
    );
  }

  static Map<String, int> _createPositionsMap(final List<ExerciseLog> exerciseLogs) {
    var counter = 1;
    return {for (final log in exerciseLogs) log.id.toString(): counter++};
  }
}
