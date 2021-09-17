import 'package:client/extensions/enum_extensions.dart';
import 'package:client/models/exercises/exercise_attribute.dart';
import 'package:client/models/training_programs/exercise_set.dart';
import 'package:client/models/training_programs/workout_exercise.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:client/widgets/training_programs/days/editing/workout_exercise_dialog.dart';
import 'package:flutter/material.dart';

class WorkoutExerciseDetailView extends StatelessWidget {
  final WorkoutExercise _workoutExercise;

  const WorkoutExerciseDetailView(this._workoutExercise, {Key? key}) : super(key: key);

  Widget _buildAttributeValues(BuildContext context, Map<ExerciseAttribute, Object> attributes) {
    var segments = <TextSegment>[];
    var attributeList = attributes.entries.toList();
    for (int i = 0; i < attributeList.length; i++) {
      final attribute = attributeList[i];
      var text = attribute.key.toValueString(attribute.value);
      segments.add(TextSegment(text, suffix: i != attributeList.length - 1 ? ', ' : null));
    }

    return Container(
      child: Expanded(
        child: SegmentedText(
          baseStyle: Theme.of(context).textTheme.bodyText2,
          segments: segments,
        ),
      ),
    );
  }

  Widget _buildSet(BuildContext context, ExerciseSet set) {
    // TODO does not look nice yet
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('Set ${set.number}: '),
        _buildAttributeValues(context, set.attributes),
      ],
    );
  }

  void _openExerciseDialog(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute<void>(
        builder: (ctx) => WorkoutExerciseDialog(_workoutExercise),
        fullscreenDialog: true,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.only(bottom: 10),
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
                  _workoutExercise.exercise.title,
                  textAlign: TextAlign.center,
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
                ..._workoutExercise.sets.map((set) => _buildSet(context, set)),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
