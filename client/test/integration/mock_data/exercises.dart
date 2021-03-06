import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_history.dart';
import 'package:client/models/exercises/exercise_history_entry.dart';
import 'package:client/models/exercises/exercise_statistics.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:timezone/timezone.dart';

ExerciseHistory exerciseHistoryForExercise(final Location timezone) => ExerciseHistory(
      [
        ExerciseHistoryEntry(
          TZDateTime.now(timezone),
          ExerciseLog(
            id: 1,
            position: 2,
            exercise: const Exercise(
              id: 1,
              name: 'Overhead Press',
              description: 'A very nice exercise',
              muscleGroups: [MuscleGroup.shoulders],
              loggingTypes: [LoggingType.reps],
            ),
            setLogs: <SetLog>[
              RepsSetLog(
                id: 1,
                exerciseLogId: 1,
                position: 2,
                weightG: 1000,
                resistanceBands: [ResistanceBand.heavy],
                reps: 23,
              ),
              TimeSetLog(
                id: 2,
                exerciseLogId: 1,
                position: 1,
                weightG: 1234,
                resistanceBands: [ResistanceBand.light, ResistanceBand.medium],
                seconds: 23,
                rpe: 8,
              )
            ],
          ),
        )
      ],
    );

ExerciseStatistics exerciseStatisticsForExercise() => const ExerciseStatistics(
      1,
      100000,
      maxReps: 5,
      maxSeconds: 60,
      estimatedOneRepMaxG: 117000,
    );

const data = [
  Exercise(
    id: 1,
    name: 'Overhead Press',
    description: 'A very nice exercise',
    muscleGroups: [MuscleGroup.shoulders],
    loggingTypes: [
      LoggingType.reps,
    ],
  ),
  Exercise(
    id: 2,
    name: 'Plank',
    description:
        'Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet.',
    muscleGroups: [MuscleGroup.abs, MuscleGroup.arms],
    loggingTypes: [LoggingType.time],
  ),
  Exercise(
    id: 3,
    name: 'Barbell Curl',
    description: 'Another nice exercise!',
    muscleGroups: [MuscleGroup.arms],
    loggingTypes: [LoggingType.reps],
  ),
  Exercise(
    id: 4,
    name: 'Jump Squats',
    description: 'Another nice exercise!',
    muscleGroups: [MuscleGroup.legs],
    loggingTypes: [
      LoggingType.reps,
    ],
  ),
  Exercise(
    id: 5,
    name: 'Dips',
    description: 'Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice Nice',
    muscleGroups: [MuscleGroup.chest, MuscleGroup.arms],
    loggingTypes: [
      LoggingType.reps,
    ],
  ),
  Exercise(
    id: 6,
    name: 'Band Curl',
    description: 'Choose the right one',
    muscleGroups: [MuscleGroup.arms],
    loggingTypes: [LoggingType.reps],
  ),
  Exercise(
    id: 7,
    name: 'Hyperextension',
    description: 'Better than superman',
    muscleGroups: [MuscleGroup.back, MuscleGroup.legs, MuscleGroup.glutes],
    loggingTypes: [LoggingType.reps],
  ),
  Exercise(
    id: 8,
    name: 'Superman',
    description: 'Better than nothing',
    muscleGroups: [MuscleGroup.back],
    loggingTypes: [LoggingType.time],
  ),
  Exercise(
    id: 9,
    name: 'Push Up',
    description: 'That is something one can do',
    muscleGroups: [MuscleGroup.chest, MuscleGroup.arms],
    loggingTypes: [LoggingType.reps],
  ),
  Exercise(
    id: 10,
    name: 'Leg Press',
    description: 'Do not bend your knees in the wrong direction!',
    muscleGroups: [MuscleGroup.legs],
    loggingTypes: [LoggingType.reps],
  ),
];

Exercise byId(final int id) {
  return data.where((final exercise) => exercise.id == id).first;
}
