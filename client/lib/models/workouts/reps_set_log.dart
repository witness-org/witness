import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:json_annotation/json_annotation.dart';

part 'reps_set_log.g.dart';

@JsonSerializable()
class RepsSetLog extends SetLog {
  RepsSetLog({
    required final int id,
    required final int exerciseLogId,
    required final int position,
    final int? rpe,
    required final int weightG,
    required final List<ResistanceBand> resistanceBands,
    required final this.reps,
  }) : super(id: id, exerciseLogId: exerciseLogId, position: position, rpe: rpe, weightG: weightG, resistanceBands: resistanceBands);

  factory RepsSetLog.fromJson(final Map<String, dynamic> json) => _$RepsSetLogFromJson(json);

  final int reps;

  Map<String, dynamic> toJson() => _$RepsSetLogToJson(this)..addEntries([const MapEntry<String, dynamic>('type', 'reps')]);
}
