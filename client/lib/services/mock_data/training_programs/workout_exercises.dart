import 'package:client/models/exercises/exercise_attribute.dart';
import 'package:client/models/training_programs/exercise_set.dart';
import 'package:client/models/training_programs/workout_exercise.dart';
import 'package:client/services/mock_data/exercises/exercises.dart' as exercises;

final data = [
  WorkoutExercise(
    id: 1,
    number: 1,
    exercise: exercises.byId(1),
    sets: [
      ExerciseSet(id: 1, number: 1, attributes: {ExerciseAttribute.reps: 20, ExerciseAttribute.weight: 15}, rpe: 5, restSeconds: 30),
      ExerciseSet(id: 2, number: 2, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.weight: 15}, rpe: 3, restSeconds: 30),
      ExerciseSet(id: 3, number: 3, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.weight: 15}, rpe: 7, restSeconds: 60),
    ],
    comment: 'Nice to have this exercise as part of this workout.',
  ),
  WorkoutExercise(
    id: 2,
    number: 2,
    exercise: exercises.byId(3),
    sets: [
      ExerciseSet(id: 4, number: 1, attributes: {ExerciseAttribute.reps: 25, ExerciseAttribute.weight: 20}, rpe: 1, restSeconds: 15),
      ExerciseSet(id: 5, number: 2, attributes: {ExerciseAttribute.reps: 20, ExerciseAttribute.weight: 15}, rpe: 8, restSeconds: 30),
    ],
    comment: 'I want it in this workout because it\'s cool.',
  ),
  WorkoutExercise(
    id: 3,
    number: 3,
    exercise: exercises.byId(4),
    sets: [
      ExerciseSet(id: 6, number: 1, attributes: {ExerciseAttribute.reps: 300, ExerciseAttribute.weight: 1}, rpe: 10, restSeconds: 600),
      ExerciseSet(id: 7, number: 2, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.weight: 15}, rpe: 1, restSeconds: 10),
    ],
    comment: 'Makes the workout even better.',
  ),
  WorkoutExercise(
    id: 4,
    number: 4,
    exercise: exercises.byId(5),
    sets: [
      ExerciseSet(
          id: 8,
          number: 1,
          attributes: {ExerciseAttribute.reps: 30, ExerciseAttribute.time: 60, ExerciseAttribute.weight: 2},
          rpe: 10,
          restSeconds: 60),
      ExerciseSet(
          id: 9,
          number: 2,
          attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.time: 40, ExerciseAttribute.weight: 5},
          rpe: 1,
          restSeconds: 10),
    ],
    // comment: 'Comment',
  ),
  WorkoutExercise(
    id: 5,
    number: 5,
    exercise: exercises.byId(6),
    sets: [
      ExerciseSet(id: 10, number: 1, attributes: {ExerciseAttribute.distance: 4}, rpe: 6, restSeconds: 45),
    ],
    comment: 'I don\'t know how this came exercise into this workout.',
  ),
  WorkoutExercise(
    id: 6,
    number: 6,
    exercise: exercises.byId(7),
    sets: [
      ExerciseSet(id: 11, number: 1, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.band: 'black'}, rpe: 4, restSeconds: 15),
      ExerciseSet(id: 12, number: 2, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.band: 'red'}, rpe: 5, restSeconds: 20),
      ExerciseSet(id: 13, number: 3, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.band: 'yellow'}, rpe: 6, restSeconds: 30),
    ],
    //comment: 'Comment',
  ),
  WorkoutExercise(
    id: 7,
    number: 7,
    exercise: exercises.byId(8),
    sets: [
      ExerciseSet(id: 14, number: 1, attributes: {ExerciseAttribute.reps: 25, ExerciseAttribute.weight: 30}, rpe: 8, restSeconds: 30),
      ExerciseSet(id: 15, number: 2, attributes: {ExerciseAttribute.reps: 20, ExerciseAttribute.weight: 30}, rpe: 7, restSeconds: 20),
      ExerciseSet(id: 16, number: 3, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.weight: 30}, rpe: 6, restSeconds: 10),
    ],
    //comment: 'Comment',
  ),
  WorkoutExercise(
    id: 8,
    number: 8,
    exercise: exercises.byId(9),
    sets: [
      ExerciseSet(id: 17, number: 1, attributes: {ExerciseAttribute.reps: 25, ExerciseAttribute.weight: 10}, rpe: 2, restSeconds: 30),
      ExerciseSet(id: 18, number: 2, attributes: {ExerciseAttribute.reps: 15, ExerciseAttribute.weight: 10}, rpe: 1, restSeconds: 30),
    ],
    comment: 'Boring comment.',
  ),
  WorkoutExercise(
    id: 9,
    number: 9,
    exercise: exercises.byId(10),
    sets: [
      ExerciseSet(id: 19, number: 1, attributes: {ExerciseAttribute.reps: 20}, rpe: 4, restSeconds: 30),
      ExerciseSet(id: 20, number: 2, attributes: {ExerciseAttribute.reps: 20}, rpe: 4, restSeconds: 30),
    ],
    //comment: 'Comment',
  ),
  WorkoutExercise(
    id: 10,
    number: 10,
    exercise: exercises.byId(11),
    sets: [
      ExerciseSet(id: 21, number: 1, attributes: {ExerciseAttribute.reps: 10, ExerciseAttribute.weight: 40}, rpe: 4, restSeconds: 30),
      ExerciseSet(id: 22, number: 2, attributes: {ExerciseAttribute.reps: 10, ExerciseAttribute.weight: 40}, rpe: 4, restSeconds: 25),
      ExerciseSet(id: 23, number: 3, attributes: {ExerciseAttribute.reps: 10, ExerciseAttribute.weight: 40}, rpe: 5, restSeconds: 25),
      ExerciseSet(id: 24, number: 4, attributes: {ExerciseAttribute.reps: 10, ExerciseAttribute.weight: 40}, rpe: 6, restSeconds: 20),
    ],
    //comment: 'Comment',
  ),
  WorkoutExercise(
    id: 11,
    number: 11,
    exercise: exercises.byId(12),
    sets: [
      ExerciseSet(id: 25, number: 1, attributes: {ExerciseAttribute.reps: 30}, rpe: 7, restSeconds: 25),
      ExerciseSet(id: 26, number: 2, attributes: {ExerciseAttribute.reps: 30}, rpe: 8, restSeconds: 25),
    ],
    //comment: 'Comment',
  ),
];

WorkoutExercise byId(int id) {
  return data.where((exercise) => exercise.id == id).first;
}
