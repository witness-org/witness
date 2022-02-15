import 'package:json_annotation/json_annotation.dart';
import 'package:timezone/timezone.dart';
import 'package:timezone/timezone.dart' as tz;

/// This class represents a custom converter for [TZDateTime] objects.
/// It implements the [fromJson] function, which is used to convert a given string from a JSON object to a [TZDateTime] object (deserialization), and
/// the [toJson] function, which is used to convert a [TZDateTime] object to a JSON representation (serialization).
///
/// The custom converter is necessary because [TZDateTime] objects cannot be (de)serialized using [JsonSerializable].
/// Note that the actual (de)serialization is delegated to the timezone library.
///
/// To use the converter, simply add an annotation to the class containing a [TZDateTime] class variable that should be serializable:
/// ```dart
/// @JsonSerializable()
/// @TZDateTimeConverter()
/// class TZDateTimeContainer {
///   const TZDateTimeContainer({
///     required final this.dateTimeField,
///   });
///
///   final TZDateTime dateTimeField;
/// }
/// ```
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
