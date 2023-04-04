import 'package:client/models/converters/tz_date_time_converter.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:timezone/timezone.dart';

part 'workout_log.g.dart';

@JsonSerializable()
@TZDateTimeConverter()
class WorkoutLog {
  const WorkoutLog({required this.id, required this.loggedOn, required this.durationMinutes, required this.exerciseLogs});

  factory WorkoutLog.fromJson(final Map<String, dynamic> json) => _$WorkoutLogFromJson(json);

  final int id;
  final TZDateTime loggedOn;
  final int durationMinutes;
  final List<ExerciseLog> exerciseLogs;
}
