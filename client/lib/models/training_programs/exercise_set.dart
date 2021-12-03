import 'package:client/models/exercises/logging_type.dart';

class ExerciseSet {
  ExerciseSet({
    required final this.id,
    required final this.number,
    final this.loggingTypes = const <LoggingType, Object>{},
    final this.rpe,
    final this.restSeconds,
  });

  final int id;
  final int number;
  final Map<LoggingType, Object> loggingTypes;
  final int? rpe;
  final int? restSeconds;
}
