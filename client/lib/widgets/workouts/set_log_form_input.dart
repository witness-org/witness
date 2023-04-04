import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/extensions/map_extensions.dart';

class SetLogFormInput {
  SetLogFormInput({
    this.id,
    this.position,
    this.rpe,
    this.weightG = 0,
    required this.loggingType,
    required this.loggedValue,
    required this.resistanceBands,
  });

  SetLogFormInput.createForm(this.loggingType);

  factory SetLogFormInput.editForm(final SetLog setLog) {
    late LoggingType loggingType;
    late int loggedValue;
    if (setLog is RepsSetLog) {
      loggingType = LoggingType.reps;
      loggedValue = setLog.reps;
    } else if (setLog is TimeSetLog) {
      loggingType = LoggingType.time;
      loggedValue = setLog.seconds;
    } else {
      throw Exception('Unsupported set log type!');
    }

    return SetLogFormInput(
      id: setLog.id,
      position: setLog.position,
      rpe: setLog.rpe,
      weightG: setLog.weightG,
      loggingType: loggingType,
      loggedValue: loggedValue,
      resistanceBands: {for (final band in ResistanceBand.values) band: setLog.resistanceBands.contains(band)},
    );
  }

  int? id;
  int? position;
  int? rpe;
  int weightG = 0;
  LoggingType loggingType;
  int loggedValue = 0;
  Map<ResistanceBand, bool> resistanceBands = {for (final band in ResistanceBand.values) band: false};

  List<ResistanceBand> get resistanceBandList {
    return resistanceBands.whereKeys((final element) => element.value).toList();
  }
}
