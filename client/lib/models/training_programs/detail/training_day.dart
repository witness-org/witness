import 'package:client/models/training_programs/workout.dart';

class TrainingDay {
  const TrainingDay({
    required final this.id,
    required final this.number,
    final this.name,
    final this.description,
    final this.workouts = const <Workout>[],
  });

  final int id;
  final int number;
  final String? name;
  final String? description;
  final List<Workout> workouts;
}
