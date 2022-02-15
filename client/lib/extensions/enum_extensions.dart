import 'package:client/extensions/cast_extensions.dart';
import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:flutter/material.dart';

/// Provides methods that facilitate handling of [LoggingType]s.
extension LoggingTypeExtensions on LoggingType {
  // TODO(raffaelfoidl-leabrugger): also localize enum representations

  /// This method is used to display the unit in the UI which is used in logs depending on the respective logging type.
  String toUiUnitString() {
    switch (this) {
      case LoggingType.reps:
        return 'reps';
      case LoggingType.time:
        return 'seconds';
      default:
        throw Exception('No UI string representation for the unit of enum member "${this}" available.');
    }
  }

  /// This method is used to get a string representation of the respective logging type to be displayed in the UI.
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

/// Provides methods that facilitate handling of [MuscleGroup]s.
extension MuscleGroupExtensions on MuscleGroup {
  /// This method is used to get a string representation of the respective muscle group to be displayed in the UI.
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

extension ResistanceBandExtensions on ResistanceBand {
  /// This method is used to get a string representation of the respective resistance band to be displayed in the UI.
  String toUiString() {
    switch (this) {
      case ResistanceBand.light:
        return 'light';
      case ResistanceBand.medium:
        return 'medium';
      case ResistanceBand.heavy:
        return 'heavy';
      case ResistanceBand.xHeavy:
        return 'X-heavy';
      case ResistanceBand.xxHeavy:
        return 'XX-heavy';
      default:
        throw Exception('No UI string representation for enum member "${this}" available.');
    }
  }

  /// This method is used for [Widget] representations of different resistance bands. The bands are distinguished in the UI by a color. The mapping
  /// of the band "value" to the color is done here.
  Color mapToColor() {
    switch (this) {
      case ResistanceBand.light:
        return Colors.green;
      case ResistanceBand.medium:
        return Colors.blue;
      case ResistanceBand.heavy:
        return Colors.yellow;
      case ResistanceBand.xHeavy:
        return Colors.red;
      case ResistanceBand.xxHeavy:
        return Colors.black;
      default:
        throw Exception('No color mapping for enum member "${this}" available.');
    }
  }
}
