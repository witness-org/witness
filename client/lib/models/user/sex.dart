import 'package:json_annotation/json_annotation.dart';

enum Sex {
  @JsonValue('MALE')
  male,

  @JsonValue('FEMALE')
  female,
}
