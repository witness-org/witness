import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/set_log_converter.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise_log.g.dart';

@JsonSerializable()
@SetLogConverter()
class ExerciseLog {
  const ExerciseLog({
    required final this.id,
    required final this.position,
    required final this.exercise,
    final this.comment,
    required final this.setLogs,
  });

  factory ExerciseLog.fromJson(final Map<String, dynamic> json) => _$ExerciseLogFromJson(json);

  final int id;
  final int position;
  final Exercise exercise;
  final String? comment;
  final List<SetLog> setLogs;
}
