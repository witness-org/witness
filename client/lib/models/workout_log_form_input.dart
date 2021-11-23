import 'package:client/models/workouts/workout_log.dart';

class WorkoutLogFormInput {
  WorkoutLogFormInput({final this.id, final this.durationMinutes = 0});

  WorkoutLogFormInput.editForm(final WorkoutLog workoutLog) : this(id: workoutLog.id, durationMinutes: workoutLog.durationMinutes);

  int? id;
  int? durationMinutes;
}
