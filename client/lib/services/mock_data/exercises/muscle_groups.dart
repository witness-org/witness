import 'package:client/models/exercises/muscle_group.dart';

const data = [
  MuscleGroup(1, 'Shoulders'),
  MuscleGroup(2, 'Abs'),
  MuscleGroup(3, 'Biceps'),
  MuscleGroup(4, 'Cardio'),
  MuscleGroup(5, 'Triceps'),
  MuscleGroup(6, 'Back'),
  MuscleGroup(7, 'Chest'),
  MuscleGroup(8, 'Legs'),
];

MuscleGroup byId(final int id) {
  return data[id - 1];
}
