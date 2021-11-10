import 'package:client/models/workouts/set_log.dart';

class ExerciseLog {
  const ExerciseLog({
    required final this.id,
    required final this.position,
    required final this.exerciseName,
    final this.comment,
    required final this.setLogs,
  });

  final int id;
  final int position;
  final int exerciseName;
  final String? comment;
  final List<SetLog> setLogs;
}
