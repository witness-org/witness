import 'package:client/models/exercises/exercise_tag.dart';

final data = const [
  const ExerciseTag(1, 'Shoulders'),
  const ExerciseTag(2, 'Abs'),
  const ExerciseTag(3, 'Biceps'),
  const ExerciseTag(4, 'Cardio'),
  const ExerciseTag(5, 'Triceps'),
  const ExerciseTag(6, 'Back'),
  const ExerciseTag(7, 'Chest'),
  const ExerciseTag(8, 'Legs'),
];

ExerciseTag byId(int id) {
  return data[id - 1];
}
