import 'package:client/models/exercise_attribute.dart';

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
      case ExerciseAttribute.time:
        return 'time';
      default:
        throw Exception('No UI representation for enum member "${this}" available.');
    }
  }
}
