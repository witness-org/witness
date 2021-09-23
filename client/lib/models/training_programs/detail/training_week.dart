import 'package:client/models/training_programs/detail/training_day.dart';

class TrainingWeek {
  const TrainingWeek({required final this.id, required final this.number, final this.description, final this.days = const <TrainingDay>[]});

  final int id;
  final int number;
  final String? description;
  final List<TrainingDay> days;
}
