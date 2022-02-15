import 'package:client/models/converters/tz_date_time_converter.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:timezone/timezone.dart';

part 'exercise_history_entry.g.dart';

@JsonSerializable()
@TZDateTimeConverter()
class ExerciseHistoryEntry {
  const ExerciseHistoryEntry(this.loggedOn, this.exerciseLog);

  factory ExerciseHistoryEntry.fromJson(final Map<String, dynamic> json) => _$ExerciseHistoryEntryFromJson(json);

  final TZDateTime loggedOn;
  final ExerciseLog exerciseLog;
}
