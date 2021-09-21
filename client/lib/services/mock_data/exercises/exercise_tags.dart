import 'package:client/models/exercises/exercise_tag.dart';

const data = [
  ExerciseTag(1, 'Shoulders'),
  ExerciseTag(2, 'Abs'),
  ExerciseTag(3, 'Biceps'),
  ExerciseTag(4, 'Cardio'),
  ExerciseTag(5, 'Triceps'),
  ExerciseTag(6, 'Back'),
  ExerciseTag(7, 'Chest'),
  ExerciseTag(8, 'Legs'),
];

ExerciseTag byId(final int id) {
  return data[id - 1];
}
