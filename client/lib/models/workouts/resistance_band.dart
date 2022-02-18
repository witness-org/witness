import 'package:json_annotation/json_annotation.dart';

enum ResistanceBand {
  @JsonValue('LIGHT')
  light,

  @JsonValue('MEDIUM')
  medium,

  @JsonValue('HEAVY')
  heavy,

  @JsonValue('X_HEAVY')
  xHeavy,

  @JsonValue('XX_HEAVY')
  xxHeavy
}
