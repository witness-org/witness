import 'package:client/models/workouts/resistance_band.dart';

abstract class SetLogCreate {
  const SetLogCreate({final this.rpe, required final this.weightG, required final this.resistanceBands});

  final int? rpe;
  final int weightG;
  final List<ResistanceBand> resistanceBands;
}
