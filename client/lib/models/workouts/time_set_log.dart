import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:json_annotation/json_annotation.dart';

part 'time_set_log.g.dart';

@JsonSerializable()
class TimeSetLog extends SetLog {
  TimeSetLog({
    required final int id,
    required final int exerciseLogId,
    required final int position,
    final int? rpe,
    required final int weightG,
    required final List<ResistanceBand> resistanceBands,
    required final this.seconds,
  }) : super(id: id, exerciseLogId: exerciseLogId, position: position, rpe: rpe, weightG: weightG, resistanceBands: resistanceBands);

  factory TimeSetLog.fromJson(final Map<String, dynamic> json) => _$TimeSetLogFromJson(json);

  final int seconds;

  Map<String, dynamic> toJson() => _$TimeSetLogToJson(this)..addEntries([const MapEntry<String, dynamic>('type', 'time')]);
}
