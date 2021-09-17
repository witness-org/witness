import 'package:client/models/training_programs/workout.dart';

class TrainingDay {
  final int id;
  final int number;
  final String? name;
  final String? description;
  final List<Workout> workouts;

  const TrainingDay({required this.id, required this.number, this.name, this.description, this.workouts = const <Workout>[]});
}
