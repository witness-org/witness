import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise.g.dart';

@JsonSerializable()
class Exercise {
  const Exercise({
    required final this.id,
    required final this.name,
    final this.description,
    final this.muscleGroups = const <MuscleGroup>[],
    final this.loggingTypes = const <LoggingType>[],
    final this.createdBy,
  });

  factory Exercise.fromJson(final Map<String, dynamic> json) => _$ExerciseFromJson(json);

  final int id;
  final String name;
  final String? description;
  final List<MuscleGroup> muscleGroups;
  final List<LoggingType> loggingTypes;
  final String? createdBy;

  Map<String, dynamic> toJson() => _$ExerciseToJson(this);
}
