import 'package:client/models/exercise_attribute.dart';
import 'package:client/models/exercise_tag.dart';

class Exercise {
  final int id;
  final String title;
  final String description;
  final Iterable<ExerciseTag> tags;
  final Iterable<ExerciseAttribute> attributes;

  const Exercise({required this.id, required this.title, this.description = '', this.tags = const [], this.attributes = const []});
}
