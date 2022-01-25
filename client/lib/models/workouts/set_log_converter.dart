import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:json_annotation/json_annotation.dart';

/// This class represents a custom converter for [SetLog] objects.
/// It implements the [fromJson] function, which is used to convert a JSON representation of a [SetLog] instance to an instance of a concrete
/// [SetLog] subclass depending on the key "type" in the map indicating the type (deserialization).
/// Furthermore, it implements the [toJson] function, which is used to convert an instance of a concrete [SetLog] subclass to a JSON representation
/// based on the concrete subtype (serialization).
/// Since the JSON representation of a [SetLog] object is a JSON object, the JSON representation is a [Map] mapping the class variable names as
/// [String]s to the values of the variables as [dynamic]s.
///
/// The custom converter is necessary because [SetLog] is an abstract class that cannot be (de)serialized with [JsonSerializable] since the
/// (de)serialization depends on the concrete class that the respective object is an instance of.
/// Note that the actual (de)serialization is delegated to the respective subclasses of [SetLog].
///
/// To use the converter, simply add an annotation to the class containing a [SetLog] class variable that should be serializable:
/// ```
/// @SetLogConverter()
/// class SetLogContainer {
///   const SetLogContainer({
///     required final this.setLogField,
///   });
///
///   final SetLog setLogField;
/// }
/// ```
class SetLogConverter implements JsonConverter<SetLog, Map<String, dynamic>> {
  const SetLogConverter();

  @override
  SetLog fromJson(final Map<String, dynamic> json) {
    if (json.containsKey('type')) {
      if (json['type'] == 'reps') {
        return RepsSetLog.fromJson(json);
      } else if (json['type'] == 'time') {
        return TimeSetLog.fromJson(json);
      }
    }

    throw Exception('Could not identify type of set log!');
  }

  @override
  Map<String, dynamic> toJson(final SetLog object) {
    if (object is RepsSetLog) {
      return object.toJson();
    } else if (object is TimeSetLog) {
      return object.toJson();
    }

    throw Exception('Could not identify type of set log!');
  }
}
