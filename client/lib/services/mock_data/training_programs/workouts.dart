import 'package:client/models/training_programs/workout.dart';
import 'package:client/services/mock_data/training_programs/workout_exercises.dart' as workout_exercises;

final data = [
  Workout(
    id: 1,
    number: 1,
    // name: 'Starter Workout', // => no blank line should appear in UI, and parentheses should be left out in day detail view
    description: 'What a nice workout!',
    exercises: [
      workout_exercises.byId(1),
      workout_exercises.byId(2),
    ],
  ),
  Workout(
    id: 2,
    number: 2,
    name: 'Regular Workout',
    // description: 'Description', // => no blank line should appear in UI
    exercises: [
      workout_exercises.byId(3),
      workout_exercises.byId(4),
      workout_exercises.byId(5),
    ],
  ),
  Workout(
    id: 3,
    number: 3,
    name: 'Workout for the Lazy',
    description: 'This is a medium long workout description ' * 2,
    exercises: [
      workout_exercises.byId(6),
    ],
  ),
  Workout(
    id: 4,
    number: 4,
    name: 'Workout for the Vigorous - We also need a long Workout name to test the capabilities of wrapping and displaying multi-line names.',
    description: 'This is a very long workout description  ' * 5,
    exercises: [
      workout_exercises.byId(7),
      workout_exercises.byId(8),
      workout_exercises.byId(9),
      workout_exercises.byId(10),
      workout_exercises.byId(11),
    ],
  ),
];

Workout byId(final int id) {
  return data.where((final workout) => workout.id == id).first;
}
