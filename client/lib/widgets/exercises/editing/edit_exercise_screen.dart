import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('edit_exercise_screen');

class EditExerciseScreen extends StatelessWidget {
  static const routeName = '/edit-exercise';
  final Exercise? _exercise;

  const EditExerciseScreen(this._exercise, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    // TODO implement according to mockups
    final title = _exercise == null ? 'Create Exercise' : 'Edit Exercise';
    return Scaffold(
      appBar: MainAppBar(
        preferredTitle: title,
      ),
      body: _exercise == null ? Center(child: Text('Creating new Exercise')) : Center(child: Text('Editing Exercise "${_exercise!.title}"')),
    );
  }
}
