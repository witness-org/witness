import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_history');

class ExerciseHistory extends StatelessWidget {
  final Exercise _exercise;

  const ExerciseHistory(this._exercise, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return Center(
      child: Text('${_exercise.title} history'),
    );
  }
}
