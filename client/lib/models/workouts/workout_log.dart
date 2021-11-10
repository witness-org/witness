import 'package:client/models/workouts/exercise_log.dart';

class WorkoutLog {
  const WorkoutLog({
    required final this.id,
    required final this.loggedOn,
    final this.durationMinutes,
    required final this.exerciseLogs,
  });

  final int id;
  final DateTime loggedOn;
  final int? durationMinutes;
  final List<ExerciseLog> exerciseLogs;
}
