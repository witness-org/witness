import 'package:client/models/training_programs/workout_exercise.dart';

class Workout {
  const Workout({
    required final this.id,
    required final this.number,
    final this.name,
    final this.description,
    final this.exercises = const <WorkoutExercise>[],
  });

  final int id;
  final int number;
  final String? name;
  final String? description;
  final List<WorkoutExercise> exercises;
}
