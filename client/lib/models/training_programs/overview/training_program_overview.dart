class TrainingProgramOverview {
  const TrainingProgramOverview({
    required final this.id,
    required final this.name,
    required final this.isPublished,
    required final this.numberOfWeeks,
    final this.description,
  });

  final int id;
  final String name;
  final bool isPublished;
  final String? description;
  final int numberOfWeeks;
}
