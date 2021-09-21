import 'package:client/models/exercises/exercise_attribute.dart';

class ExerciseSet {
  ExerciseSet({
    required final this.id,
    required final this.number,
    final this.attributes = const <ExerciseAttribute, Object>{},
    final this.rpe,
    final this.restSeconds,
  });

  final int id;
  final int number;
  final Map<ExerciseAttribute, Object> attributes;
  final int? rpe;
  final int? restSeconds;
}
