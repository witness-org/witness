import 'package:client/extensions/cast_extensions.dart';
import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/exercises/exercise_attribute.dart';

extension ExerciseAttributeExtensions on ExerciseAttribute {
  String toUiString() {
    // TODO also localize enum representations
    switch (this) {
      case ExerciseAttribute.band:
        return 'band';
      case ExerciseAttribute.distance:
        return 'distance';
      case ExerciseAttribute.reps:
        return 'reps';
      case ExerciseAttribute.time:
        return 'time';
      case ExerciseAttribute.weight:
        return 'weight';
      default:
        throw Exception('No UI string representation for enum member "${this}" available.');
    }
  }

  String toValueString(Object value) {
    switch (this) {
      case ExerciseAttribute.band:
        return '$value ${this.toUiString()}';
      case ExerciseAttribute.distance:
        final typedValue = value.castOrThrow<num>();
        return '${this.toUiString()}: $typedValue';
      case ExerciseAttribute.reps:
        final typedValue = value.castOrThrow<int>();
        return '${typedValue.toNumberString('rep')}';
      case ExerciseAttribute.time:
        final typedValue = value.castOrThrow<num>();
        return '${this.toUiString()}: $typedValue';
      case ExerciseAttribute.weight:
        final typedValue = value.castOrThrow<num>();
        return '${this.toUiString()}: $typedValue';
      default:
        throw Exception('No UI representation for enum member "${this}" available.');
    }
  }
}
