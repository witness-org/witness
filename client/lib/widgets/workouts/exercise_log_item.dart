import 'package:client/logging/log_message_preparer.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/set_log_item.dart';
import 'package:flutter/material.dart';

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
      subtitle: TextButton(
        child: Row(children: [
          const Icon(Icons.align_horizontal_left_outlined, size: 15),
          const SizedBox(width: 5),
          Text(
            exerciseLog.comment ?? '',
          ),
        ]),
        style: TextButton.styleFrom(
          primary: Colors.grey,
          tapTargetSize: MaterialTapTargetSize.shrinkWrap,
        ),
        onPressed: () {},
      ),
    );
  }

  Widget _buildSetLogList(final List<SetLog> setLogs) {
    return Padding(
      padding: const EdgeInsets.only(left: 7, right: 7),
      child: Column(
        children: [
          ...setLogs.map((final item) {
            return SetLogItem(item);
          }).toList()
        ],
      ),
    );
  }

  Widget _buildButtonRow() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: <Widget>[
        TextButton(
          child: const Text('DELETE'),
          onPressed: () {},
        ),
        const SizedBox(width: 8),
        TextButton(
          child: const Text('ADD SET'),
          onPressed: () {},
        ),
        const SizedBox(width: 8),
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
    return Card(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: <Widget>[_buildExerciseCardTitleAndSubtitle(_exerciseLog!), _buildSetLogList(_exerciseLog!.setLogs), _buildButtonRow()],
      ),
    );
  }
}
