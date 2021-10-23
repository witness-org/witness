import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_statistics');

class ExerciseStatistics extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const ExerciseStatistics(this._exercise, {final Key? key}) : super(key: key);

  final Exercise _exercise;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Center(
      child: Text('${_exercise.name} ${uiStrings.exerciseStatistics_placeholder_suffix}'),
    );
  }
}
