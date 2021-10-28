import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/extensions/map_extensions.dart';

class ExerciseFormInput {
  ExerciseFormInput({
    final this.id,
    final this.name = '',
    final this.description,
    required final this.muscleGroups,
    required final this.loggingTypes,
  });

  ExerciseFormInput.createForm({final this.name = ''});

  ExerciseFormInput.editForm(final Exercise exercise)
      : this(
            id: exercise.id,
            name: exercise.name,
            description: exercise.description,
            muscleGroups: {for (final group in MuscleGroup.values) group: exercise.muscleGroups.contains(group)},
            loggingTypes: {for (final type in LoggingType.values) type: exercise.loggingTypes.contains(type)});

  int? id;
  String name;
  String? description;
  Map<MuscleGroup, bool> muscleGroups = {for (final group in MuscleGroup.values) group: false};
  Map<LoggingType, bool> loggingTypes = {for (final type in LoggingType.values) type: false};

  String? get descriptionNullOrNotEmpty {
    return description != null && description!.isEmpty ? null : description;
  }

  List<MuscleGroup> get muscleGroupList {
    return muscleGroups.whereKeys((final element) => element.value).toList();
  }

  List<LoggingType> get loggingTypeList {
    return loggingTypes.whereKeys((final element) => element.value).toList();
  }
}
