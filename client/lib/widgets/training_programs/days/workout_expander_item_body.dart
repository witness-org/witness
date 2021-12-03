import 'package:client/models/training_programs/workout.dart';
import 'package:client/widgets/training_programs/days/workout_exercise_detail_view.dart';
import 'package:flutter/material.dart';

class WorkoutExpanderItemBody extends StatelessWidget {
  const WorkoutExpanderItemBody(this._workout, {final Key? key}) : super(key: key);

  final Workout _workout;

  List<Widget> _buildDescriptionSection() {
    return _workout.description != null
        ? [
            Text(
              _workout.description!,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 5),
          ]
        : [];
  }

  @override
  Widget build(final BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, bottom: 15),
      child: Align(
        alignment: Alignment.topLeft,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ..._buildDescriptionSection(),
            ..._workout.exercises.map((final e) => WorkoutExerciseDetailView(e)),
          ],
        ),
      ),
    );
  }
}
