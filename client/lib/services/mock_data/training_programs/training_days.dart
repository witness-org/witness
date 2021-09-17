import 'package:client/models/training_programs/detail/training_day.dart';
import 'package:client/services/mock_data/training_programs/workouts.dart' as workouts;

final data = [
  TrainingDay(
    id: 1,
    number: 1,
    name: 'Light Day: ' + ('Long Text ' * 15),
    // description: 'Day Description ' * 2, // => no empty line should be rendered in UI
    workouts: [
      workouts.byId(1),
    ],
  ),
  TrainingDay(
    id: 2,
    number: 2,
    name: 'Feelgood Day',
    description: 'This is a regular day Description ' * 2,
    workouts: [
      workouts.byId(2),
    ],
  ),
  TrainingDay(
    id: 3,
    number: 3,
    name: 'Ambitious Day',
    description: 'Another nice description ' * 3,
    workouts: [
      workouts.byId(1),
      workouts.byId(2),
    ],
  ),
  TrainingDay(
    id: 4,
    number: 4,
    name: 'All-Out Day',
    description: 'This is a rather long day description ' * 5,
    workouts: [
      workouts.byId(1),
      workouts.byId(2),
      workouts.byId(3),
      workouts.byId(4),
    ],
  ),
  TrainingDay(
    id: 5,
    number: 5,
    name: 'Cool-Down Day',
    description: 'Another description ' * 2,
    workouts: [
      workouts.byId(4),
      workouts.byId(1),
    ],
  ),
  TrainingDay(
    id: 6,
    number: 6,
    name: 'Lazy Day',
    description: 'This day deserves a very long description ' * 4,
    workouts: [
      workouts.byId(3),
    ],
  ),
  TrainingDay(
    id: 7,
    number: 7,
    name: 'Show-Off Day',
    description: 'Too lazy for a description. ',
    workouts: [
      workouts.byId(4),
    ],
  ),
];

TrainingDay byId(int id) {
  return data.where((day) => day.id == id).first;
}
