import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/set_log_converter.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise_log_create.g.dart';

@JsonSerializable()
@SetLogConverter()
class ExerciseLogCreate {
  const ExerciseLogCreate({required final this.exerciseId, final this.comment, required final this.setLogs});

  ExerciseLogCreate.empty(final int exerciseId) : this(exerciseId: exerciseId, setLogs: []);

  final int exerciseId;
  final String? comment;
  final List<SetLog> setLogs;

  Map<String, dynamic> toJson() => _$ExerciseLogCreateToJson(this);
}
