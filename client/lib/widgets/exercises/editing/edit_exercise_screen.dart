import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('edit_exercise_screen');

class EditExerciseScreen extends StatelessWidget with LogMessagePreparer {
  const EditExerciseScreen(this._exercise, {final Key? key}) : super(key: key);

  static const routeName = '/edit-exercise';
  final Exercise? _exercise;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    // TODO(raffaelfoidl-leabrugger): implement according to mockups
    final title = _exercise == null ? 'Create Exercise' : 'Edit Exercise';
    return Scaffold(
      appBar: MainAppBar(
        preferredTitle: title,
      ),
      body: _exercise == null ? const Center(child: Text('Creating new Exercise')) : Center(child: Text('Editing Exercise "${_exercise!.title}"')),
    );
  }
}
