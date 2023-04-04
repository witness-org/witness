import 'package:client/models/training_programs/detail/training_day.dart';

class TrainingWeek {
  const TrainingWeek({required this.id, required this.number, this.description, this.days = const <TrainingDay>[]});

  final int id;
  final int number;
  final String? description;
  final List<TrainingDay> days;
}
