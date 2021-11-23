import 'package:json_annotation/json_annotation.dart';
import 'package:timezone/timezone.dart';
import 'package:timezone/timezone.dart' as tz;

class TZDateTimeConverter implements JsonConverter<TZDateTime, String> {
  const TZDateTimeConverter();

  @override
  TZDateTime fromJson(final String json) {
    return TZDateTime.parse(tz.local, json);
  }

  @override
  String toJson(final TZDateTime object) {
    return object.toIso8601String();
  }
}
