import 'package:client/models/training_programs/detail/training_week.dart';
import 'package:client/services/mock_data/training_programs/training_days.dart' as training_days;

final data = [
  TrainingWeek(
    id: 1,
    number: 1,
    description: 'Body Warmup',
    days: [
      training_days.byId(1),
      training_days.byId(2),
      training_days.byId(3),
      training_days.byId(4),
    ],
  ),
  TrainingWeek(
    id: 2,
    number: 2,
    description: 'Body Grill',
    days: [
      training_days.byId(3),
      training_days.byId(7),
      training_days.byId(5),
    ],
  ),
  TrainingWeek(
    id: 3,
    number: 3,
    description: 'Body Cooldown',
    days: [
      training_days.byId(1),
      training_days.byId(5),
      training_days.byId(7),
      training_days.byId(6),
    ],
  ),
  TrainingWeek(
    id: 4,
    number: 4,
    description: 'Summer Hit',
    days: [
      training_days.byId(7),
      training_days.byId(6),
      training_days.byId(3),
      training_days.byId(4),
      training_days.byId(5),
      training_days.byId(1),
      training_days.byId(2),
    ],
  ),
  TrainingWeek(
    id: 5,
    number: 5,
    description: 'Autumn Breeze',
    days: [
      training_days.byId(4),
      training_days.byId(3),
      training_days.byId(2),
      training_days.byId(1),
    ],
  ),
  TrainingWeek(
    id: 6,
    number: 6,
    description: 'Winter Chill',
    days: [
      training_days.byId(5),
      training_days.byId(7),
      training_days.byId(2),
    ],
  ),
  TrainingWeek(
    id: 7,
    number: 7,
    description: 'Spring Lift',
    days: [
      training_days.byId(5),
      training_days.byId(7),
      training_days.byId(3),
      training_days.byId(4),
      training_days.byId(2),
    ],
  )
];

TrainingWeek byId(final int id) {
  return data.where((final week) => week.id == id).first;
}
