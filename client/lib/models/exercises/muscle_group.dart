import 'package:json_annotation/json_annotation.dart';

enum MuscleGroup {
  @JsonValue('CHEST')
  chest,

  @JsonValue('SHOULDERS')
  shoulders,

  @JsonValue('BACK')
  back,

  @JsonValue('LEGS')
  legs,

  @JsonValue('ABS')
  abs,

  @JsonValue('ARMS')
  arms,

  @JsonValue('GLUTES')
  glutes,

  @JsonValue('OTHER')
  other
}
