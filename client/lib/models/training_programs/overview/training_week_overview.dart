class TrainingWeekOverview {
  const TrainingWeekOverview({
    required final this.id,
    required final this.number,
    final this.description,
    required final this.numberOfDays,
    required final this.totalNumberOfWorkouts,
  });

  final int id;
  final int number;
  final String? description;
  final int numberOfDays;
  final int totalNumberOfWorkouts;
}
