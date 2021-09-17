class TrainingWeekOverview {
  final int id;
  final int number;
  final String? description;
  final int numberOfDays;
  final int totalNumberOfWorkouts;

  const TrainingWeekOverview({
    required this.id,
    required this.number,
    this.description,
    required this.numberOfDays,
    required this.totalNumberOfWorkouts,
  });
}
