import 'package:client/models/training_programs/workout.dart';
import 'package:client/widgets/training_programs/days/workout_exercise_detail_view.dart';
import 'package:flutter/material.dart';

class WorkoutExpanderItemBody extends StatelessWidget {
  final Workout _workout;

  const WorkoutExpanderItemBody(this._workout, {Key? key}) : super(key: key);

  List<Widget> _buildDescriptionSection() {
    return _workout.description != null
        ? [
            Text(
              _workout.description!,
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 5),
          ]
        : [];
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, bottom: 15),
      child: Align(
        alignment: Alignment.topLeft,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ..._buildDescriptionSection(),
            ..._workout.exercises.map((e) => WorkoutExerciseDetailView(e)),
          ],
        ),
      ),
    );
  }
}
