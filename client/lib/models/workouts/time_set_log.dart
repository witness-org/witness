import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:json_annotation/json_annotation.dart';

part 'time_set_log.g.dart';

@JsonSerializable()
class TimeSetLog extends SetLog {
  TimeSetLog({
    required final int id,
    required final int position,
    final int? rpe,
    required final int weightKg,
    required final List<ResistanceBand> resistanceBands,
    required final this.seconds,
  }) : super(id: id, position: position, rpe: rpe, weightKg: weightKg, resistanceBands: resistanceBands);

  factory TimeSetLog.fromJson(final Map<String, dynamic> json) => _$TimeSetLogFromJson(json);

  final int seconds;
}
