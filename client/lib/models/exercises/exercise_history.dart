import 'package:client/models/exercises/exercise_history_entry.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise_history.g.dart';

@JsonSerializable()
class ExerciseHistory {
  const ExerciseHistory(this.entries);

  factory ExerciseHistory.fromJson(final Map<String, dynamic> json) => _$ExerciseHistoryFromJson(json);

  final List<ExerciseHistoryEntry> entries;
}
