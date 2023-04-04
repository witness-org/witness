import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:json_annotation/json_annotation.dart';

part 'exercise_create.g.dart';

@JsonSerializable()
class ExerciseCreate {
  ExerciseCreate({required this.name, this.description, required this.muscleGroups, required this.loggingTypes});

  factory ExerciseCreate.fromJson(final Map<String, dynamic> json) => _$ExerciseCreateFromJson(json);

  final String name;
  final String? description;
  final List<MuscleGroup> muscleGroups;
  final List<LoggingType> loggingTypes;

  Map<String, dynamic> toJson() => _$ExerciseCreateToJson(this);
}
