import 'package:client/models/exercises/exercise_attribute.dart';
import 'package:client/models/exercises/exercise_tag.dart';

class Exercise {
  final int id;
  final String title;
  final String? description;
  final List<ExerciseTag> tags;
  final List<ExerciseAttribute> attributes;

  const Exercise({
    required this.id,
    required this.title,
    this.description,
    this.tags = const <ExerciseTag>[],
    this.attributes = const <ExerciseAttribute>[],
  });
}
