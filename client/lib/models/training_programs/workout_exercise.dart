import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/training_programs/exercise_set.dart';

class WorkoutExercise {
  final int id;
  final int number;
  final Exercise exercise;
  final List<ExerciseSet> sets;
  final String? comment;

  const WorkoutExercise({
    required this.id,
    required this.number,
    required this.exercise,
    this.sets = const <ExerciseSet>[],
    this.comment,
  });
}
