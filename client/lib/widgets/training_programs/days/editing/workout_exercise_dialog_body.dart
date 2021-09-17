import 'package:client/models/training_programs/workout_exercise.dart';
import 'package:client/widgets/training_programs/days/editing/workout_exercise_set_view.dart';
import 'package:flutter/material.dart';

class WorkoutExerciseDialogBody extends StatelessWidget {
  final WorkoutExercise _exercise;

  const WorkoutExerciseDialogBody(this._exercise, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Scrollbar(
        isAlwaysShown: true,
        child: ListView.builder(
            itemCount: _exercise.sets.length,
            itemBuilder: (_, index) {
              return Padding(
                padding: const EdgeInsets.only(left: 16, right: 16, bottom: 16),
                child: WorkoutExerciseSetView(_exercise.sets[index]),
              );
            }),
      ),
    );
  }
}
