import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/set_log_create.dart';
import 'package:client/models/converters/set_log_create_converter.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise_log_create.g.dart';

@JsonSerializable()
@SetLogCreateConverter()
class ExerciseLogCreate {
  const ExerciseLogCreate({required this.exerciseId, this.comment, required this.setLogs});

  ExerciseLogCreate.empty(final int exerciseId) : this(exerciseId: exerciseId, setLogs: []);

  ExerciseLogCreate.fromExerciseLog(final ExerciseLog exerciseLog)
      : this(
          exerciseId: exerciseLog.exercise.id,
          comment: exerciseLog.comment,
          setLogs: exerciseLog.setLogs.map(SetLogCreate.fromSetLog).toList(),
        );

  factory ExerciseLogCreate.fromJson(final Map<String, dynamic> json) => _$ExerciseLogCreateFromJson(json);

  final int exerciseId;
  final String? comment;
  final List<SetLogCreate> setLogs;

  Map<String, dynamic> toJson() => _$ExerciseLogCreateToJson(this);
}
