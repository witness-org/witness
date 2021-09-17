class TrainingDayOverview {
  final int id;
  final int number;
  final String? name;
  final String? description;
  final int numberOfWorkouts;

  const TrainingDayOverview({required this.id, required this.number, this.name, this.description, required this.numberOfWorkouts});
}
