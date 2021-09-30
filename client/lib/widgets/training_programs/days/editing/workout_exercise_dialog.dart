import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/workout_exercise.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:client/widgets/training_programs/common/training_program_component_header.dart';
import 'package:client/widgets/training_programs/days/editing/workout_exercise_dialog_body.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('workout_exercise_dialog');

// TODO(raffaelfoidl-leabrugger): We might need to discuss whether we want to be able to save changes made to a specific set independently or
//  (as it is right now) just save (or discard) all changes made to all sets at once

// TODO(raffaelfoidl-leabrugger): Add new attributes, remove attributes (currently, only attribute values are displayed to edit)
class WorkoutExerciseDialog extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const WorkoutExerciseDialog(final this._exercise, {final Key? key}) : super(key: key);

  final WorkoutExercise _exercise;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final theme = Theme.of(context);
    final uiStrings = getLocalizedStrings(context);
    return Scaffold(
      appBar: AppBar(
        title: Text(uiStrings.workoutExerciseDialog_appBar_title),
        actions: [
          IconButton(
            onPressed: () => Navigator.of(context).pop(),
            icon: const Icon(Icons.save),
            tooltip: uiStrings.workoutExerciseDialog_appBar_save,
          )
        ],
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 16, top: 16, right: 16, bottom: 8),
            child: TrainingProgramComponentHeader(
              [
                SegmentedText(
                  baseStyle: theme.textTheme.bodyText2?.merge(const TextStyle(fontSize: 16)),
                  segments: [
                    TextSegment(
                      '${uiStrings.workoutExerciseDialog_header_numberPrefix} ${_exercise.number}',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    TextSegment(_exercise.exercise.title, prefix: ' (', suffix: ')'),
                  ],
                ),
                if (_exercise.comment != null) Text(_exercise.comment!),
                if (_exercise.exercise.description != null) Text(_exercise.exercise.description!), // show both comment and description? or only one?
                const SizedBox(height: 3),
              ],
            ),
          ),
          WorkoutExerciseDialogBody(_exercise),
        ],
      ),
    );
  }
}
