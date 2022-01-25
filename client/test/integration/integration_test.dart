import 'dart:convert';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/reps_set_log.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/time_set_log.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/services/exercise_service.dart';
import 'package:client/services/server_response.dart';
import 'package:client/services/workout_log_service.dart';
import 'package:client/widgets/common/image_provider_facade.dart';
import 'package:client/widgets/exercises/exercises_by_muscle_group_screen.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/workouts/exercise_log_item.dart';
import 'package:client/widgets/workouts/set_logs_table.dart';
import 'package:client/widgets/workouts/workout_log_item.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_test/src/matchers.dart' as matchers;
import 'package:http/http.dart';
import 'package:injector/injector.dart';
import 'package:integration_test/integration_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:timezone/timezone.dart';
import 'package:timezone/timezone.dart' as tz;

import '../common/test_helpers.dart';
import 'integration_test.mocks.dart';
import 'integration_test_utils.dart';
import 'mock_data/exercises.dart' as mock_exercises;

const _sutName = 'integration_test';

final _localTimezone = tz.local;

/// This custom mock is needed because Mockito cannot generate stubs for functions with generic return types, but BaseService (which is needed in the
/// integration tests as the integration tests access mocks of services extending BaseService) contains the function decodeResponse which does have a
/// generic return type. Hence, the solution is to register a custom mock for all services extending BaseService and adding a mapping from the
/// original method to the custom mock method to the fallbackGenerators parameter.
/// Note that the fallback values will never be returned from a real method call. They are only used internally by Mockito as valid return values.
///
/// For example, in the [GenerateMocks] annotation, register a custom mock of a service extending BaseService as follows:
/// ```
/// @GenerateMocks([...], customMocks: [
///  ...,
///   MockSpec<ExampleService>(fallbackGenerators: {#decodeResponse: mockDecodeResponse}),
/// ])
/// ```
///
/// See https://github.com/dart-lang/mockito/blob/master/NULL_SAFETY_README.md#fallback-generators for further information.
T mockDecodeResponse<T>(final Response? httpResponse) {
  if (httpResponse != null) {
    return json.decode(utf8.decode(httpResponse.bodyBytes)) as T;
  }

  throw Exception('Expected response, but there was none!');
}

@GenerateMocks([], customMocks: [
  MockSpec<ExerciseService>(fallbackGenerators: {#decodeResponse: mockDecodeResponse}),
  MockSpec<WorkoutLogService>(returnNullOnMissingStub: true, fallbackGenerators: {#decodeResponse: mockDecodeResponse}),
  MockSpec<ImageProviderFacade>(returnNullOnMissingStub: true),
])
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  Future<void> navigateToScreen(final String drawerTileKey, final WidgetTester tester) async {
    // verify drawer has opened correctly by asserting existence of requested list tile
    await openDrawer(tester);
    expectWidgetByKey(drawerTileKey, matchers.findsOneWidget);

    // after tapping the requested tile, the drawer should be closed
    await tapByKey(drawerTileKey, tester);
  }

  setUpAll(() {
    // set up interception of getLocalTimezone() method call from flutter_native_timezone since the method accesses native features which are not
    // available if the integration tests are not run using an emulator
    TestDefaultBinaryMessengerBinding.instance?.defaultBinaryMessenger.setMockMethodCallHandler(
      const MethodChannel('flutter_native_timezone'),
      (final MethodCall methodCall) async {
        // if the getLocalTimezone() function from flutter_native_timezone is called, return 'Europe/Vienna'
        if (methodCall.method == 'getLocalTimezone') {
          return 'Europe/Vienna';
        }
      },
    );
  });

  group(getPrefixedGroupName(_sutName, 'startup and authentication:'), () {
    testWidgets('application should start up successfully', (final WidgetTester tester) async {
      await initTest(tester, login: false);

      // most basic test: root element should exist, i.e. app should be up and running successfully
      expectWidgetByKey('root', matchers.findsOneWidget);
    });

    testWidgets('application should show login form as initial screen', (final WidgetTester tester) async {
      await initTest(tester, login: false);

      // we are at the login form
      expectWidgetByKey('login_card', matchers.findsOneWidget);

      // there is an email and password textbox, but no password confirmation textbox (would be sign up mode)
      expectWidgetByKey('login_card.email', matchers.findsOneWidget);
      expectWidgetByKey('login_card.password', matchers.findsOneWidget);
      expectWidgetByKey('login_card.passwordConfirmation', matchers.findsNothing);
    });

    testWidgets('login form should allow switching to sign up mode', (final WidgetTester tester) async {
      await initTest(tester, login: false);

      // we are at the login form
      expectWidgetByKey('login_card', matchers.findsOneWidget);

      // initially, we are in login mode (email, password textbox, but no password confirmation)
      expectWidgetByKey('login_card.email', matchers.findsOneWidget);
      expectWidgetByKey('login_card.password', matchers.findsOneWidget);
      expectWidgetByKey('login_card.passwordConfirmation', matchers.findsNothing);

      // after clicking the "switch auth mode" button, we are in sign up mode (email, password and password confirmation textbox)
      await tapByKey('login_card.switchAuthMode', tester);
      expectWidgetByKey('login_card.email', matchers.findsOneWidget);
      expectWidgetByKey('login_card.password', matchers.findsOneWidget);
      expectWidgetByKey('login_card.passwordConfirmation', matchers.findsOneWidget);
    });

    testWidgets('fake-login for integration tests works', (final WidgetTester tester) async {
      await initTest(tester);

      // since our FirebaseAuth fake object feigns a signed in user, the login form should not appear
      expectWidgetByKey('login_card', matchers.findsNothing);
      expectWidgetByKey('workout_log_screen', matchers.findsOneWidget);
    });
  });

  group(getPrefixedGroupName(_sutName, 'exercises:'), () {
    testWidgets('can navigate to exercises screen', (final WidgetTester tester) async {
      await initTest(tester);

      // after tapping the exercises tile in the drawer, the exercise screen should be focused
      await navigateToScreen('app_drawer.exercises', tester);
      expectWidgetByKey('exercises_screen', matchers.findsOneWidget);
    });

    testWidgets('there are muscle groups to select from', (final WidgetTester tester) async {
      await initTest(tester);

      // focus exercise screen
      await navigateToScreen('app_drawer.exercises', tester);
      expectWidgetByKey('exercises_screen', matchers.findsOneWidget);

      // there should be exercise overview items corresponding to the domain model's muscle groups
      expectWidgetByType(ExerciseOverviewItem, matchers.findsWidgets);
    });

    testWidgets('receives mocked exercises for selected muscle group', (final WidgetTester tester) async {
      final mockExerciseService = MockExerciseService();
      when(mockExerciseService.getExercisesByMuscleGroup(any, any)).thenAnswer((final _) async => const ServerResponse.success(mock_exercises.data));

      await initTest(tester, login: true, additionalDependencyOverrides: (final Injector injector) {
        registerSingleton<ExerciseService, MockExerciseService>(injector, () => mockExerciseService);
      });

      // focus exercise screen
      await navigateToScreen('app_drawer.exercises', tester);
      expectWidgetByKey('exercises_screen', matchers.findsOneWidget);

      // there should be exercise overview items corresponding to the domain model's muscle groups
      expectWidgetByType(ExerciseOverviewItem, matchers.findsWidgets);

      // when tapping on of those muscle group items, the respective screen should open up with exercises returned by the mocked ExerciseService
      final firstOverviewItem = findByType(ExerciseOverviewItem).first;
      await tap(firstOverviewItem, tester);

      expectWidgetByType(ExerciseByMuscleGroupItem, matchers.findsWidgets);
    });
  });

  group(getPrefixedGroupName(_sutName, 'workout logs:'), () {
    testWidgets('displays mocked workout logs', (final WidgetTester tester) async {
      // override default empty mock server response to return some workout logs
      final mockWorkoutLogService = MockWorkoutLogService();
      when(mockWorkoutLogService.getWorkoutLogsByDate(any, any)).thenAnswer(
        (final _) async => ServerResponse.success([
          WorkoutLog(exerciseLogs: [], id: 1, loggedOn: TZDateTime.now(_localTimezone), durationMinutes: 60),
          WorkoutLog(exerciseLogs: [], id: 2, loggedOn: TZDateTime.now(_localTimezone), durationMinutes: 60),
        ]),
      );

      await initTest(tester, login: true, additionalDependencyOverrides: (final Injector injector) {
        registerSingleton<WorkoutLogService, MockWorkoutLogService>(injector, () => mockWorkoutLogService);
      });

      // although it is the "home screen", focus workout log screen to be sure
      await navigateToScreen('app_drawer.workout_log', tester);
      expectWidgetByKey('workout_log_screen', matchers.findsOneWidget);
      expectWidgetByType(WorkoutLogItem, matchers.findsWidgets);
    });

    testWidgets('displays exercise logs of mocked workout log', (final WidgetTester tester) async {
      // override default empty mock server response to return some workout logs
      final mockWorkoutLogService = MockWorkoutLogService();
      when(mockWorkoutLogService.getWorkoutLogsByDate(any, any)).thenAnswer(
        (final _) async => ServerResponse.success(
          [
            WorkoutLog(
              id: 1,
              loggedOn: TZDateTime.now(_localTimezone),
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
                      loggingTypes: [
                        LoggingType.reps,
                      ],
                    ),
                    setLogs: List.from(<SetLog>[]))
              ],
              durationMinutes: 60,
            ),
          ],
        ),
      );

      await initTest(tester, login: true, additionalDependencyOverrides: (final Injector injector) {
        registerSingleton<WorkoutLogService, MockWorkoutLogService>(injector, () => mockWorkoutLogService);
      });

      // although it is the "home screen", focus workout log screen to be sure
      await navigateToScreen('app_drawer.workout_log', tester);
      expectWidgetByKey('workout_log_screen', matchers.findsOneWidget);

      // verify workout logs with its stubbed exercise logs have been fetched
      expectWidgetByTypeAndText(WorkoutLogItem, 'Workout 1', matchers.findsWidgets);
      expectWidgetByTypeAndContainingText(ExerciseLogItem, 'Barbell', matchers.findsOneWidget);
      expectWidgetByTypeAndContainingText(ExerciseLogItem, 'Overhead', matchers.findsOneWidget);
      // less strict (if somethings fails due to small display or in CI)
      // expectWidgetByType(WorkoutLogItem, matchers.findsWidgets);
      // expectWidgetByType(ExerciseLogItem, matchers.findsWidgets);
    });

    testWidgets('displays set logs of mocked workout log', (final WidgetTester tester) async {
      // override default empty mock server response to return some workout logs
      final mockWorkoutLogService = MockWorkoutLogService();
      when(mockWorkoutLogService.getWorkoutLogsByDate(any, any)).thenAnswer(
        (final _) async => ServerResponse.success(
          [
            WorkoutLog(
              id: 3,
              loggedOn: TZDateTime.now(_localTimezone),
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
            ),
          ],
        ),
      );

      await initTest(tester, login: true, additionalDependencyOverrides: (final Injector injector) {
        registerSingleton<WorkoutLogService, MockWorkoutLogService>(injector, () => mockWorkoutLogService);
      });

      // although it is the "home screen", focus workout log screen to be sure
      await navigateToScreen('app_drawer.workout_log', tester);
      expectWidgetByKey('workout_log_screen', matchers.findsOneWidget);

      // since collapsible cards (ExpansionTiles) are collapsed by default, we need to expand it before verifying expectations
      final logCard = findByType(ExpansionTile).first;
      await tap(logCard, tester);

      // verify workout log with its stubbed exercise and set logs has been fetched
      expectWidgetByTypeAndText(WorkoutLogItem, 'Workout 1', matchers.findsWidgets);
      expectWidgetByTypeAndContainingText(SetLogsTable, RegExp(r'1(\.|,)234'), matchers.findsWidgets);
      expectWidgetByTypeAndContainingText(SetLogsTable, 'RPE', matchers.findsWidgets);
      expectWidgetByTypeAndContainingText(SetLogsTable, 's', matchers.findsOneWidget);
      expectWidgetByTypeAndText(SetLogsTable, 'kg', matchers.findsWidgets);
    });
  });
}
