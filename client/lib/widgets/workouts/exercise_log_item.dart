import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/set_logs_table.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_log_item');

class ExerciseLogItem extends StatefulWidget with LogMessagePreparer {
  const ExerciseLogItem(this.exerciseLog, {final Key? key}) : super(key: key);
  final ExerciseLog exerciseLog;

  @override
  State<StatefulWidget> createState() => ExerciseLogItemState();
}

class ExerciseLogItemState extends State<ExerciseLogItem> with StringLocalizer, LogMessagePreparer {
  ExerciseLog? _exerciseLog;

  Widget _buildExerciseCardTitleAndSubtitle(final ExerciseLog exerciseLog) {
    return ListTile(
      title: Text(exerciseLog.exerciseName),
      subtitle: exerciseLog.comment != null
          ? TextButton(
              child: Row(
                children: [
                  const Icon(Icons.align_horizontal_left_outlined, size: 15),
                  const SizedBox(width: 5),
                  Text(
                    exerciseLog.comment!,
                  ),
                ],
              ),
              style: TextButton.styleFrom(
                primary: Colors.grey,
                tapTargetSize: MaterialTapTargetSize.shrinkWrap,
              ),
              onPressed: () {},
            )
          : null,
    );
  }

  Widget _buildSetLogList(final List<SetLog> setLogs) {
    return Padding(
      padding: const EdgeInsets.only(left: 14, right: 14),
      child: SetLogTable(setLogs),
    );
  }

  List<Widget> _buildButton(final String text) {
    return [
      TextButton(
        child: Text(text),
        onPressed: () {},
      ),
      const SizedBox(width: 8),
    ];
  }

  Widget _buildButtonRow(final bool addCommentButton, final StringLocalizations uiStrings) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        ..._buildButton(uiStrings.exerciseLogItem_delete),
        ..._buildButton(uiStrings.exerciseLogItem_addSet),
        if (addCommentButton) ..._buildButton(uiStrings.exerciseLogItem_addComment),
      ],
    );
  }

  @override
  void initState() {
    super.initState();

    _exerciseLog = widget.exerciseLog;
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Card(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[
          _buildExerciseCardTitleAndSubtitle(_exerciseLog!),
          _buildSetLogList(_exerciseLog!.setLogs),
          _buildButtonRow(_exerciseLog!.comment == null, uiStrings),
        ],
      ),
    );
  }
}
