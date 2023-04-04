class TrainingProgramOverview {
  const TrainingProgramOverview({
    required this.id,
    required this.name,
    required this.isPublished,
    required this.numberOfWeeks,
    this.description,
  });

  final int id;
  final String name;
  final bool isPublished;
  final String? description;
  final int numberOfWeeks;
}
