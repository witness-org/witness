import 'package:client/models/workouts/resistance_band.dart';

abstract class SetLog {
  const SetLog({
    required final this.id,
    required final this.exerciseLogId,
    required final this.position,
    final this.rpe,
    required final this.weightG,
    required final this.resistanceBands,
  });

  final int id;
  final int exerciseLogId;
  final int position;
  final int? rpe;
  final int weightG;
  final List<ResistanceBand> resistanceBands;
}
