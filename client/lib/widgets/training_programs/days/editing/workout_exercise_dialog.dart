import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/workout_exercise.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:client/widgets/training_programs/common/training_program_component_header.dart';
import 'package:client/widgets/training_programs/days/editing/workout_exercise_dialog_body.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('workout_exercise_dialog');

// TODO we might need to discuss whether we want to be able to save changes made to a specific set independently or (as it is right now)
// just save (or discard) all changes made to all sets at once

// TODO add new attributes, remove attributes (currently, only attribute values are displayed to edit)
class WorkoutExerciseDialog extends StatelessWidget {
  final WorkoutExercise _exercise;

  const WorkoutExerciseDialog(this._exercise, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(
        title: Text('Exercise Detail View'),
        actions: [
          IconButton(
            onPressed: () => Navigator.of(context).pop(),
            icon: Icon(Icons.save),
            tooltip: 'Save',
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
                    TextSegment('Exercise ${_exercise.id}', style: const TextStyle(fontWeight: FontWeight.bold)),
                    TextSegment(_exercise.exercise.title, prefix: ' (', suffix: ')'),
                  ],
                ),
                if (_exercise.comment != null) Text(_exercise.comment!),
                if (_exercise.exercise.description != null) Text(_exercise.exercise.description!), // show both comment and description? or only one?
                SizedBox(height: 3),
              ],
            ),
          ),
          WorkoutExerciseDialogBody(_exercise),
        ],
      ),
    );
  }
}
