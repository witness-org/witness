import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:json_annotation/json_annotation.dart';

part 'reps_set_log.g.dart';

@JsonSerializable()
class RepsSetLog extends SetLog {
  RepsSetLog({
    required final int id,
    required final int position,
    final int? rpe,
    required final int weightKg,
    required final List<ResistanceBand> resistanceBands,
    required final this.reps,
  }) : super(id: id, position: position, rpe: rpe, weightKg: weightKg, resistanceBands: resistanceBands);

  factory RepsSetLog.fromJson(final Map<String, dynamic> json) => _$RepsSetLogFromJson(json);

  final int reps;
}
