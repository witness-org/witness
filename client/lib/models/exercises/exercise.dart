import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise.g.dart';

@JsonSerializable()
class Exercise {
  const Exercise({
    required this.id,
    required this.name,
    this.description,
    this.muscleGroups = const <MuscleGroup>[],
    this.loggingTypes = const <LoggingType>[],
    this.createdBy,
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
