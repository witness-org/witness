import 'package:client/models/training_programs/workout_exercise.dart';
import 'package:client/widgets/training_programs/days/editing/workout_exercise_set_view.dart';
import 'package:flutter/material.dart';

class WorkoutExerciseDialogBody extends StatelessWidget {
  const WorkoutExerciseDialogBody(this._exercise, {final Key? key}) : super(key: key);

  final WorkoutExercise _exercise;

  @override
  Widget build(final BuildContext context) {
    return Expanded(
      child: Scrollbar(
        thumbVisibility: true,
        child: ListView.builder(
            itemCount: _exercise.sets.length,
            itemBuilder: (final _, final index) {
              return Padding(
                padding: const EdgeInsets.only(left: 16, right: 16, bottom: 16),
                child: WorkoutExerciseSetView(_exercise.sets[index]),
              );
            }),
      ),
    );
  }
}
