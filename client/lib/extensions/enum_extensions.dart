import 'package:client/extensions/cast_extensions.dart';
import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';

extension LoggingTypeExtensions on LoggingType {
  // TODO(raffaelfoidl-leabrugger): also localize enum representations

  String toUiString() {
    switch (this) {
      case LoggingType.reps:
        return 'reps';
      case LoggingType.time:
        return 'time';
      default:
        throw Exception('No UI string representation for enum member "${this}" available.');
    }
  }

  String toValueString(final Object value) {
    switch (this) {
      case LoggingType.reps:
        final typedValue = value.castOrThrow<int>();
        return typedValue.toNumberString('rep');
      case LoggingType.time:
        final typedValue = value.castOrThrow<num>();
        return '${toUiString()}: $typedValue';
      default:
        throw Exception('No UI representation for enum member "${this}" available.');
    }
  }
}

extension MuscleGroupExtension on MuscleGroup {
  String toUiString() {
    switch (this) {
      case MuscleGroup.chest:
        return 'Chest';
      case MuscleGroup.shoulders:
        return 'Shoulders';
      case MuscleGroup.back:
        return 'Back';
      case MuscleGroup.legs:
        return 'Legs';
      case MuscleGroup.abs:
        return 'Abs';
      case MuscleGroup.arms:
        return 'Arms';
      case MuscleGroup.glutes:
        return 'Glutes';
      case MuscleGroup.other:
        return 'Other';
      default:
        throw Exception('No UI string representation for enum member "${this}" available.');
    }
  }

  /// This method is used in requests sent to the server. Hence, the representation returned by this method must match the value of the
  /// `@JsonValue` annotations of the enum member definitions.
  String toDtoString() {
    switch (this) {
      case MuscleGroup.chest:
        return 'CHEST';
      case MuscleGroup.shoulders:
        return 'SHOULDERS';
      case MuscleGroup.back:
        return 'BACK';
      case MuscleGroup.legs:
        return 'LEGS';
      case MuscleGroup.abs:
        return 'ABS';
      case MuscleGroup.arms:
        return 'ARMS';
      case MuscleGroup.glutes:
        return 'GLUTES';
      case MuscleGroup.other:
        return 'OTHER';
      default:
        throw Exception('No string representation for enum member "${this}" available.');
    }
  }
}
