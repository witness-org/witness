import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:timezone/timezone.dart';

List<WorkoutLog> emptyWorkoutLogs(final Location timezone) => [
      WorkoutLog(exerciseLogs: [], id: 1, loggedOn: TZDateTime.now(timezone), durationMinutes: 60),
      WorkoutLog(exerciseLogs: [], id: 2, loggedOn: TZDateTime.now(timezone), durationMinutes: 60),
    ];

WorkoutLog workoutLogWithExerciseLogs(final Location timezone) => WorkoutLog(
      id: 1,
      loggedOn: TZDateTime.now(timezone),
      exerciseLogs: [
        ExerciseLog(
            id: 1,
            position: 2,
            exercise: const Exercise(
              id: 3,
              name: 'Barbell Curl',
              description: 'Another nice exercise!',
              muscleGroups: [MuscleGroup.arms],
              loggingTypes: [LoggingType.reps],
            ),
            setLogs: List.from(<SetLog>[])),
        ExerciseLog(
            id: 2,
            position: 1,
            exercise: const Exercise(
              id: 1,
              name: 'Overhead Press',
              description: 'A very nice exercise',
              muscleGroups: [MuscleGroup.shoulders],
              loggingTypes: [LoggingType.reps],
            ),
            setLogs: List.from(<SetLog>[]))
      ],
      durationMinutes: 60,
    );

WorkoutLog workoutLogWithExerciseAndSetLogs(final Location timezone) => WorkoutLog(
      id: 3,
      loggedOn: TZDateTime.now(timezone),
      exerciseLogs: [
        ExerciseLog(
          id: 1,
          position: 2,
          exercise: const Exercise(
            id: 3,
            name: 'Barbell Curl',
            description: 'Another nice exercise!',
            muscleGroups: [MuscleGroup.arms],
            loggingTypes: [LoggingType.reps],
          ),
          setLogs: <SetLog>[
            RepsSetLog(id: 1, exerciseLogId: 1, position: 2, weightG: 1000, resistanceBands: [ResistanceBand.heavy], reps: 23),
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
      ],
      durationMinutes: 60,
    );
