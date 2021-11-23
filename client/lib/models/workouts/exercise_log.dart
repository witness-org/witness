import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise_log.g.dart';

@JsonSerializable()
@_SetLogConverter()
class ExerciseLog {
  const ExerciseLog({
    required final this.id,
    required final this.position,
    required final this.exerciseName,
    final this.comment,
    required final this.setLogs,
  });

  factory ExerciseLog.fromJson(final Map<String, dynamic> json) => _$ExerciseLogFromJson(json);

  final int id;
  final int position;
  final String exerciseName;
  final String? comment;
  final List<SetLog> setLogs;
}

class _SetLogConverter implements JsonConverter<SetLog, Map<String, dynamic>> {
  const _SetLogConverter();

  @override
  SetLog fromJson(final Map<String, dynamic> json) {
    if (json.containsKey('reps')) {
      return RepsSetLog.fromJson(json);
    } else if (json.containsKey('seconds')) {
      return TimeSetLog.fromJson(json);
    }

    throw Exception('Could not identify type of set log!');
  }

  @override
  Map<String, dynamic> toJson(final SetLog object) {
    // TODO(lea): implement toJson
    throw UnimplementedError();
  }
}
