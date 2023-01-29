import 'package:client/models/training_programs/detail/training_week.dart';

class TrainingProgram {
  const TrainingProgram({
    required this.id,
    required this.name,
    required this.isPublished,
    this.description,
    this.weeks = const <TrainingWeek>[],
  });

  final int id;
  final String name;
  final bool isPublished;
  final String? description;
  final List<TrainingWeek> weeks;
}
