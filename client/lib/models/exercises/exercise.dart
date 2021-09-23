import 'package:client/models/exercises/exercise_attribute.dart';
import 'package:client/models/exercises/muscle_group.dart';

class Exercise {
  const Exercise({
    required final this.id,
    required final this.title,
    final this.description,
    final this.muscleGroups = const <MuscleGroup>[],
    final this.attributes = const <ExerciseAttribute>[],
  });

  final int id;
  final String title;
  final String? description;
  final List<MuscleGroup> muscleGroups;
  final List<ExerciseAttribute> attributes;
}
