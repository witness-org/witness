import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_history');

class ExerciseHistory extends StatelessWidget with LogMessagePreparer {
  const ExerciseHistory(this._exercise, {final Key? key}) : super(key: key);

  final Exercise _exercise;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return Center(
      child: Text('${_exercise.title} history'),
    );
  }
}
