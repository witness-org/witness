import 'package:client/models/workouts/workout_log.dart';

extension WorkoutLogExtensions on WorkoutLog {
  /// Sorts the log entries of the current [WorkoutLog] according to their positions in ascending order. More precisely, the items of the
  /// `exerciseLogs` collection are sorted based on the `position` property of the respective exercise log items, in ascending order. Furthermore,
  /// the items of the respective `setLogs` collection of those exercise logs are sorted based on the `position` property of the respective set log
  /// items, in ascending order.
  void sortLogs() {
    exerciseLogs.sort((final a, final b) => a.position.compareTo(b.position));
    for (final element in exerciseLogs) {
      element.setLogs.sort((final a, final b) => a.position.compareTo(b.position));
    }
  }
}
