import 'package:client/models/converters/tz_date_time_converter.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:timezone/timezone.dart';
import 'package:client/models/workouts/exercise_log_create.dart';

part 'workout_log_create.g.dart';

@JsonSerializable()
@TZDateTimeConverter()
class WorkoutLogCreate {
  const WorkoutLogCreate({required this.loggedOn, this.durationMinutes = 0, required this.exerciseLogs});

  WorkoutLogCreate.empty(final TZDateTime loggedOn) : this(loggedOn: loggedOn, exerciseLogs: []);

  factory WorkoutLogCreate.fromJson(final Map<String, dynamic> json) => _$WorkoutLogCreateFromJson(json);

  final TZDateTime loggedOn;
  final int durationMinutes;
  final List<ExerciseLogCreate> exerciseLogs;

  Map<String, dynamic> toJson() => _$WorkoutLogCreateToJson(this);
}
