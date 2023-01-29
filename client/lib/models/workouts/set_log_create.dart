import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/reps_set_log_create.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/models/workouts/time_set_log_create.dart';

abstract class SetLogCreate {
  const SetLogCreate({this.rpe, required this.weightG, required this.resistanceBands});

  factory SetLogCreate.fromSetLog(final SetLog setLog) {
    if (setLog is RepsSetLog) {
      return RepsSetLogCreate(rpe: setLog.rpe, weightG: setLog.weightG, resistanceBands: setLog.resistanceBands, reps: setLog.reps);
    } else if (setLog is TimeSetLog) {
      return TimeSetLogCreate(rpe: setLog.rpe, weightG: setLog.weightG, resistanceBands: setLog.resistanceBands, seconds: setLog.seconds);
    }

    throw Exception('Could not identify type of set log!');
  }

  final int? rpe;
  final int weightG;

  final List<ResistanceBand> resistanceBands;
}
