class TrainingDayOverview {
  const TrainingDayOverview({
    required final this.id,
    required final this.number,
    final this.name,
    final this.description,
    required final this.numberOfWorkouts,
  });

  final int id;
  final int number;
  final String? name;
  final String? description;
  final int numberOfWorkouts;
}
