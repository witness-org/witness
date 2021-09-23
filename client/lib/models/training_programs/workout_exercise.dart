import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/training_programs/exercise_set.dart';

class WorkoutExercise {
  const WorkoutExercise({
    required final this.id,
    required final this.number,
    required final this.exercise,
    final this.sets = const <ExerciseSet>[],
    final this.comment,
  });

  final int id;
  final int number;
  final Exercise exercise;
  final List<ExerciseSet> sets;
  final String? comment;
}
