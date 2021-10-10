import 'package:json_annotation/json_annotation.dart';

enum Role {
  @JsonValue('PREMIUM')
  premium,

  @JsonValue('ADMIN')
  admin,
}
