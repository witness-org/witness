import 'package:client/models/exercises/exercise_attribute.dart';

class ExerciseSet {
  final int id;
  final int number;
  final Map<ExerciseAttribute, Object> attributes;
  final int? rpe;
  final int? restSeconds;

  ExerciseSet({required this.id, required this.number, this.attributes = const <ExerciseAttribute, Object>{}, this.rpe, this.restSeconds});
}
