import 'package:client/models/exercises/exercise_attribute.dart';
import 'package:client/models/exercises/exercise_tag.dart';

class Exercise {
  const Exercise({
    required final this.id,
    required final this.title,
    final this.description,
    final this.tags = const <ExerciseTag>[],
    final this.attributes = const <ExerciseAttribute>[],
  });

  final int id;
  final String title;
  final String? description;
  final List<ExerciseTag> tags;
  final List<ExerciseAttribute> attributes;
}
