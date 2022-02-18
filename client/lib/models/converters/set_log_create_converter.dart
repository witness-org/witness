import 'package:client/models/workouts/reps_set_log_create.dart';
import 'package:client/models/workouts/set_log_create.dart';
import 'package:client/models/workouts/time_set_log_create.dart';
import 'package:json_annotation/json_annotation.dart';

/// This class represents a custom converter for [SetLogCreate] objects.
/// It implements the [fromJson] function, which is used to convert a JSON representation of a [SetLogCreate] instance to an instance of a concrete
/// [SetLogCreate] subclass depending on the key "type" in the map indicating the type (deserialization).
/// Furthermore, it implements the [toJson] function, which is used to convert an instance of a concrete [SetLogCreate] subclass to a JSON
/// representation based on the concrete subtype (serialization).
/// Since the JSON representation of a [SetLogCreate] object is a JSON object, the JSON representation is a [Map] mapping the class variable names as
/// [String]s to the values of the variables as [dynamic]s.
///
/// The custom converter is necessary because [SetLogCreate] is an abstract class that cannot be (de)serialized with [JsonSerializable] since the
/// (de)serialization depends on the concrete class that the respective object is an instance of.
/// Note that the actual (de)serialization is delegated to the respective subclasses of [SetLogCreate].
///
/// To use the converter, simply add an annotation to the class containing a [SetLogCreate] class variable that should be serializable:
/// ```
/// @JsonSerializable()
/// @SetLogCreateConverter()
/// class SetLogCreateContainer {
///   const SetLogCreateContainer({
///     required final this.setLogField,
///   });
///
///   final SetLogCreate setLogField;
/// }
/// ```
class SetLogCreateConverter implements JsonConverter<SetLogCreate, Map<String, dynamic>> {
  const SetLogCreateConverter();

  @override
  SetLogCreate fromJson(final Map<String, dynamic> json) {
    if (json.containsKey('type')) {
      if (json['type'] == 'repsCreate') {
        return RepsSetLogCreate.fromJson(json);
      } else if (json['type'] == 'timeCreate') {
        return TimeSetLogCreate.fromJson(json);
      }
    }

    throw Exception('Could not identify type of set log!');
  }

  @override
  Map<String, dynamic> toJson(final SetLogCreate object) {
    if (object is RepsSetLogCreate) {
      return object.toJson();
    } else if (object is TimeSetLogCreate) {
      return object.toJson();
    }

    throw Exception('Could not identify type of set log!');
  }
}
