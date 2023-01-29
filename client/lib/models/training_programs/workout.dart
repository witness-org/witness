import 'package:client/models/training_programs/workout_exercise.dart';

class Workout {
  const Workout({
    required this.id,
    required this.number,
    this.name,
    this.description,
    this.exercises = const <WorkoutExercise>[],
  });

  final int id;
  final int number;
  final String? name;
  final String? description;
  final List<WorkoutExercise> exercises;
}
