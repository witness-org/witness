import 'package:client/models/workouts/resistance_band.dart';

abstract class SetLog {
  const SetLog({
    required this.id,
    required this.exerciseLogId,
    required this.position,
    this.rpe,
    required this.weightG,
    required this.resistanceBands,
  });

  final int id;
  final int exerciseLogId;
  final int position;
  final int? rpe;
  final int weightG;
  final List<ResistanceBand> resistanceBands;
}
