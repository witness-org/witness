import 'package:client/extensions/enum_extensions.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/training_programs/exercise_set.dart';
import 'package:client/models/training_programs/workout_exercise.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:client/widgets/training_programs/days/editing/workout_exercise_dialog.dart';
import 'package:flutter/material.dart';

class WorkoutExerciseDetailView extends StatelessWidget with StringLocalizer {
  const WorkoutExerciseDetailView(this._workoutExercise, {final Key? key}) : super(key: key);

  final WorkoutExercise _workoutExercise;

  Widget _buildAttributeValues(final BuildContext context, final Map<LoggingType, Object> attributes) {
    final segments = <TextSegment>[];
    final attributeList = attributes.entries.toList();
    for (int i = 0; i < attributeList.length; i++) {
      final attribute = attributeList[i];
      final text = attribute.key.toValueString(attribute.value);
      segments.add(TextSegment(text, suffix: i != attributeList.length - 1 ? ', ' : null));
    }

    return Expanded(
      child: SegmentedText(
        baseStyle: Theme.of(context).textTheme.bodyMedium,
        segments: segments,
      ),
    );
  }

  Widget _buildSet(final BuildContext context, final StringLocalizations uiStrings, final ExerciseSet set) {
    // TODO(raffaelfoidl-leabrugger): does not look nice yet
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('${uiStrings.workoutExerciseDetailView_setNumber_prefix} ${set.number}: '),
        _buildAttributeValues(context, set.loggingTypes),
      ],
    );
  }

  void _openExerciseDialog(final BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute<void>(
        builder: (final ctx) => WorkoutExerciseDialog(_workoutExercise),
        fullscreenDialog: true,
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      child: InkWell(
        onTap: () => _openExerciseDialog(context),
        child: Container(
          width: double.infinity,
          decoration: BoxDecoration(border: Border.all(color: Colors.grey), borderRadius: BorderRadius.circular(10)),
          child: Padding(
            padding: const EdgeInsets.all(8),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  _workoutExercise.exercise.name,
                  textAlign: TextAlign.center,
                  style: const TextStyle(fontWeight: FontWeight.bold),
                ),
                ..._workoutExercise.sets.map((final set) => _buildSet(context, uiStrings, set)),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
