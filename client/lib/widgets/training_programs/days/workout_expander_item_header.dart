import 'package:client/models/training_programs/workout.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:flutter/material.dart';

class WorkoutExpanderItemHeader extends StatelessWidget {
  final Workout _workout;

  const WorkoutExpanderItemHeader(this._workout, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: SegmentedText(
        baseStyle: Theme.of(context).textTheme.subtitle1,
        segments: [
          TextSegment(
            'Workout ${_workout.number}',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          TextSegment(_workout.name, prefix: ' (', suffix: ')'),
        ],
      ),
    );
  }
}
