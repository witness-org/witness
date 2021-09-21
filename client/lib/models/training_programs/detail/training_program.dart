import 'package:client/models/training_programs/detail/training_week.dart';

class TrainingProgram {
  const TrainingProgram({
    required final this.id,
    required final this.name,
    required final this.isPublished,
    final this.description,
    final this.weeks = const <TrainingWeek>[],
  });

  final int id;
  final String name;
  final bool isPublished;
  final String? description;
  final List<TrainingWeek> weeks;
}
