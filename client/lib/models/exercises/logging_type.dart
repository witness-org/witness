import 'package:json_annotation/json_annotation.dart';

enum LoggingType {
  @JsonValue('REPS')
  reps,

  @JsonValue('TIME')
  time
}
