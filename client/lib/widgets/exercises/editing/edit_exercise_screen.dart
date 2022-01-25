import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/editing/create_exercise_form.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_form.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('edit_exercise_screen');

class EditExerciseScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const EditExerciseScreen(this._exercise, {final Key? key}) : super(key: key);

  static const routeName = '/edit-exercise';
  final Exercise? _exercise;

  Widget _buildCreateScreen() {
    return const CreateExerciseForm();
  }

  Widget _buildEditScreen(final Exercise exercise) {
    return EditExerciseForm(exercise);
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    final title = _exercise == null ? uiStrings.editExerciseScreen_appBar_title_create : uiStrings.editExerciseScreen_appBar_title_edit;
    return Scaffold(
      appBar: MainAppBar(
        preferredTitle: title,
      ),
      body: _exercise == null ? _buildCreateScreen() : _buildEditScreen(_exercise!),
    );
  }
}
