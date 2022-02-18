import 'package:json_annotation/json_annotation.dart';

part 'exercise_statistics.g.dart';

@JsonSerializable()
class ExerciseStatistics {
  const ExerciseStatistics(this.exerciseId, this.maxWeightG, {this.estimatedOneRepMaxG, this.maxReps, this.maxSeconds});

  factory ExerciseStatistics.fromJson(final Map<String, dynamic> json) => _$ExerciseStatisticsFromJson(json);

  final int exerciseId;
  final int maxWeightG;
  final int? estimatedOneRepMaxG;
  final int? maxReps;
  final int? maxSeconds;
}
