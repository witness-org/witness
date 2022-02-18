import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log_create.dart';
import 'package:json_annotation/json_annotation.dart';

part 'time_set_log_create.g.dart';

@JsonSerializable()
class TimeSetLogCreate extends SetLogCreate {
  TimeSetLogCreate({
    final int? rpe,
    required final int weightG,
    required final List<ResistanceBand> resistanceBands,
    required final this.seconds,
  }) : super(rpe: rpe, weightG: weightG, resistanceBands: resistanceBands);

  factory TimeSetLogCreate.fromJson(final Map<String, dynamic> json) => _$TimeSetLogCreateFromJson(json);

  final int seconds;

  Map<String, dynamic> toJson() => _$TimeSetLogCreateToJson(this)..addEntries([const MapEntry<String, dynamic>('type', 'timeCreate')]);
}
