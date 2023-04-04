import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log_create.dart';
import 'package:json_annotation/json_annotation.dart';

part 'reps_set_log_create.g.dart';

@JsonSerializable()
class RepsSetLogCreate extends SetLogCreate {
  RepsSetLogCreate({
    final int? rpe,
    required final int weightG,
    required final List<ResistanceBand> resistanceBands,
    required this.reps,
  }) : super(rpe: rpe, weightG: weightG, resistanceBands: resistanceBands);

  factory RepsSetLogCreate.fromJson(final Map<String, dynamic> json) => _$RepsSetLogCreateFromJson(json);

  final int reps;

  Map<String, dynamic> toJson() => _$RepsSetLogCreateToJson(this)..addEntries([const MapEntry<String, dynamic>('type', 'repsCreate')]);
}
