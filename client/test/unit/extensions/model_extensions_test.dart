import 'package:client/extensions/model_extensions.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:timezone/timezone.dart';

import '../../common/test_helpers.dart';

const _sutName = 'model_extensions_test';

void main() {
  group(getPrefixedGroupName(_sutName, 'WorkoutLogExtensions'), () {
    group('sortLogs', () {
      test('sorts exercise logs in ascending order', () {
        final workoutLog = _createWorkoutLogWithOnlyExerciseLogs();
        final expectedIdOrder = [4, 1, 2, 3];

        workoutLog.sortLogs();
        final sortedIds = workoutLog.exerciseLogs.map((final exerciseLog) => exerciseLog.id);

        expect(sortedIds, expectedIdOrder);
      });

      test('sorts set logs in ascending order', () {
        final workoutLog = _createWorkoutLogWithOneExerciseLogAndSetLogs();
        final expectedIOrder = [2, 4, 3, 1];

        workoutLog.sortLogs();
        final sortedIds = workoutLog.exerciseLogs[0].setLogs.map((final setLog) => setLog.id);

        expect(sortedIds, expectedIOrder);
      });

      test('sorts exercise logs and set logs in ascending order', () {
        final workoutLog = _createWorkoutLogWithExerciseLogsAndSetLogs();
        final expectedIdOrderExerciseLogs = [2, 3, 1];
        final expectedIdOrderSetLogs1 = [1, 3, 2];
        final expectedIdOrderSetLogs2 = [3, 2, 1];
        final expectedIdOrderSetLogs3 = [3, 1, 2];

        workoutLog.sortLogs();
        final sortedIdsExerciseLogs = workoutLog.exerciseLogs.map((final exerciseLog) => exerciseLog.id);
        final sortedIdsSetLogs1 = workoutLog.exerciseLogs.firstWhere((final log) => log.id == 1).setLogs.map((final setLog) => setLog.id);
        final sortedIdsSetLogs2 = workoutLog.exerciseLogs.firstWhere((final log) => log.id == 2).setLogs.map((final setLog) => setLog.id);
        final sortedIdsSetLogs3 = workoutLog.exerciseLogs.firstWhere((final log) => log.id == 3).setLogs.map((final setLog) => setLog.id);

        expect(sortedIdsExerciseLogs, expectedIdOrderExerciseLogs);
        expect(sortedIdsSetLogs1, expectedIdOrderSetLogs1);
        expect(sortedIdsSetLogs2, expectedIdOrderSetLogs2);
        expect(sortedIdsSetLogs3, expectedIdOrderSetLogs3);
      });
    });
  });
}

const _stubExercise = Exercise(
  id: 3,
  name: 'Barbell Curl',
  description: 'Another nice exercise!',
  muscleGroups: [MuscleGroup.arms],
  loggingTypes: [LoggingType.reps],
);

WorkoutLog _createWorkoutLogWithOnlyExerciseLogs() => WorkoutLog(
      id: 1,
      loggedOn: TZDateTime.utc(2022),
      exerciseLogs: List.from(<ExerciseLog>[
        ExerciseLog(id: 1, position: 2, exercise: _stubExercise, setLogs: List.from(<SetLog>[])),
        ExerciseLog(id: 2, position: 3, exercise: _stubExercise, setLogs: List.from(<SetLog>[])),
        ExerciseLog(id: 3, position: 4, exercise: _stubExercise, setLogs: List.from(<SetLog>[])),
        ExerciseLog(id: 4, position: 1, exercise: _stubExercise, setLogs: List.from(<SetLog>[]))
      ]),
      durationMinutes: 60,
    );

WorkoutLog _createWorkoutLogWithOneExerciseLogAndSetLogs() => WorkoutLog(
      id: 1,
      loggedOn: TZDateTime.utc(2022),
      exerciseLogs: List.from(
        <ExerciseLog>[
          ExerciseLog(
            id: 1,
            position: 1,
            exercise: _stubExercise,
            setLogs: List.from(<SetLog>[
              RepsSetLog(id: 1, exerciseLogId: 1, position: 4, weightG: 1000, resistanceBands: [], reps: 23),
              TimeSetLog(id: 2, exerciseLogId: 1, position: 1, weightG: 1234, resistanceBands: [], seconds: 23, rpe: 8),
              RepsSetLog(id: 3, exerciseLogId: 1, position: 3, weightG: 1000, resistanceBands: [], reps: 23),
              TimeSetLog(id: 4, exerciseLogId: 1, position: 2, weightG: 1234, resistanceBands: [], seconds: 23, rpe: 8)
            ]),
          ),
        ],
      ),
      durationMinutes: 60,
    );

WorkoutLog _createWorkoutLogWithExerciseLogsAndSetLogs() => WorkoutLog(
      id: 1,
      loggedOn: TZDateTime.utc(2022),
      exerciseLogs: List.from(
        <ExerciseLog>[
          ExerciseLog(
            id: 1,
            position: 3,
            exercise: _stubExercise,
            setLogs: List.from(<SetLog>[
              RepsSetLog(id: 1, exerciseLogId: 1, position: 1, weightG: 1000, resistanceBands: [], reps: 23),
              TimeSetLog(id: 2, exerciseLogId: 1, position: 3, weightG: 1234, resistanceBands: [], seconds: 23, rpe: 8),
              RepsSetLog(id: 3, exerciseLogId: 1, position: 2, weightG: 1000, resistanceBands: [], reps: 23),
            ]),
          ),
          ExerciseLog(
            id: 2,
            position: 1,
            exercise: _stubExercise,
            setLogs: List.from(<SetLog>[
              RepsSetLog(id: 1, exerciseLogId: 2, position: 3, weightG: 1000, resistanceBands: [], reps: 23),
              RepsSetLog(id: 2, exerciseLogId: 2, position: 2, weightG: 1000, resistanceBands: [], reps: 23),
              TimeSetLog(id: 3, exerciseLogId: 2, position: 1, weightG: 1234, resistanceBands: [], seconds: 23, rpe: 8)
            ]),
          ),
          ExerciseLog(
            id: 3,
            position: 2,
            exercise: _stubExercise,
            setLogs: List.from(<SetLog>[
              RepsSetLog(id: 1, exerciseLogId: 3, position: 2, weightG: 1000, resistanceBands: [], reps: 23),
              RepsSetLog(id: 2, exerciseLogId: 3, position: 3, weightG: 1000, resistanceBands: [], reps: 23),
              TimeSetLog(id: 3, exerciseLogId: 3, position: 1, weightG: 1234, resistanceBands: [], seconds: 23, rpe: 8)
            ]),
          )
        ],
      ),
      durationMinutes: 60,
    );
