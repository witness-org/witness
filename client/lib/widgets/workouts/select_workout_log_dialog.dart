import 'package:client/logging/log_message_preparer.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:flutter/material.dart';
import 'dart:math' as math;

final _logger = getLogger('select_workout_log_dialog');

class SelectWorkoutLogDialog extends StatefulWidget {
  const SelectWorkoutLogDialog(this._workoutLogs, this._selectWorkoutLogAction, {final Key? key}) : super(key: key);

  final List<WorkoutLog> _workoutLogs;
  final void Function(WorkoutLog selectedWorkoutLog) _selectWorkoutLogAction;

  @override
  State<StatefulWidget> createState() => _SelectWorkoutLogDialogState();
}

class _SelectWorkoutLogDialogState extends State<SelectWorkoutLogDialog> with StringLocalizer, LogMessagePreparer {
  Widget _buildWorkoutLogList(final StringLocalizations uiStrings, final List<WorkoutLog> workoutLogs) {
    return SizedBox(
      width: 330,
      height: math.min(workoutLogs.length * 45 + 40, 175),
      child: Scrollbar(
        child: ListView.builder(
          shrinkWrap: true,
          itemCount: workoutLogs.length,
          itemBuilder: (final _, final index) {
            final workoutLog = workoutLogs[index];
            return ListTile(
              leading: const Icon(Icons.arrow_forward_outlined),
              contentPadding: EdgeInsets.zero,
              horizontalTitleGap: 0,
              onTap: () {
                widget._selectWorkoutLogAction(workoutLog);
                Navigator.pop(context, true);
              },
              title: Text(uiStrings.workoutLogScreen_selectWorkoutLogDialog_workoutLogHeading(index + 1)),
            );
          },
        ),
      ),
    );
  }

  List<Widget> _buildActionButtons(final StringLocalizations uiStrings) {
    return [
      TextButton(
        onPressed: () => Navigator.pop(context, false),
        child: Text(uiStrings.workoutLogScreen_selectWorkoutLogDialog_cancel),
      ),
    ];
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return AlertDialog(
      scrollable: true,
      title: Text(uiStrings.workoutLogScreen_selectWorkoutLogDialog_title),
      content: _buildWorkoutLogList(uiStrings, widget._workoutLogs),
      actions: _buildActionButtons(uiStrings),
    );
  }
}
