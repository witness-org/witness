import 'package:client/models/workouts/resistance_band.dart';

abstract class SetLog {
  const SetLog({
    required final this.id,
    required final this.position,
    final this.rpe,
    required final this.weightKg,
    required final this.resistanceBands,
  });

  final int id;
  final int position;
  final int? rpe;
  final int weightKg;
  final List<ResistanceBand> resistanceBands;
}
