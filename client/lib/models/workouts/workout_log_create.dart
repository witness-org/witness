import 'package:client/models/common/tz_date_time_converter.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:timezone/timezone.dart';

part 'workout_log_create.g.dart';

@JsonSerializable()
@TZDateTimeConverter()
class WorkoutLogCreate {
  const WorkoutLogCreate({required final this.loggedOn, final this.durationMinutes = 0, required final this.exerciseLogs});

  WorkoutLogCreate.empty(final TZDateTime loggedOn) : this(loggedOn: loggedOn, exerciseLogs: []);

  factory WorkoutLogCreate.fromJson(final Map<String, dynamic> json) => _$WorkoutLogCreateFromJson(json);

  final TZDateTime loggedOn;
  final int durationMinutes;
  final List<ExerciseLog> exerciseLogs;

  Map<String, dynamic> toJson() => _$WorkoutLogCreateToJson(this);
}
