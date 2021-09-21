import 'package:client/models/training_programs/workout.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:flutter/material.dart';

class WorkoutExpanderItemHeader extends StatelessWidget {
  const WorkoutExpanderItemHeader(this._workout, {final Key? key}) : super(key: key);

  final Workout _workout;

  @override
  Widget build(final BuildContext context) {
    return ListTile(
      title: SegmentedText(
        baseStyle: Theme.of(context).textTheme.subtitle1,
        segments: [
          TextSegment(
            'Workout ${_workout.number}',
            style: const TextStyle(fontWeight: FontWeight.bold),
          ),
          TextSegment(_workout.name, prefix: ' (', suffix: ')'),
        ],
      ),
    );
  }
}
