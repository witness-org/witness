import 'package:client/models/exercises/logging_type.dart';

class ExerciseSet {
  ExerciseSet({
    required this.id,
    required this.number,
    this.loggingTypes = const <LoggingType, Object>{},
    this.rpe,
    this.restSeconds,
  });

  final int id;
  final int number;
  final Map<LoggingType, Object> loggingTypes;
  final int? rpe;
  final int? restSeconds;
}
