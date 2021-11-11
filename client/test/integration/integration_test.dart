import 'package:client/services/exercise_service.dart';
import 'package:client/services/server_response.dart';
import 'package:client/services/workout_log_service.dart';
import 'package:client/widgets/common/image_provider_facade.dart';
import 'package:client/widgets/exercises/exercises_by_muscle_group_screen.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_test/src/matchers.dart' as matchers;
import 'package:injector/injector.dart';
import 'package:integration_test/integration_test.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';

import '../common/test_helpers.dart';
import 'integration_test.mocks.dart';
import 'integration_test_utils.dart';
import 'mock_data/exercises.dart' as mock_exercises;

const _sutName = 'integration_test';

@GenerateMocks([ExerciseService, WorkoutLogService], customMocks: [MockSpec<ImageProviderFacade>(returnNullOnMissingStub: true)])
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  Future<void> navigateToScreen(final String drawerTileKey, final WidgetTester tester) async {
    // verify drawer has opened correctly by asserting existence of requested list tile
    await openDrawer(tester);
    expectWidgetByKey(drawerTileKey, matchers.findsOneWidget);

    // after tapping the requested tile, the drawer should be closed
    await tapByKey(drawerTileKey, tester);
  }

  group(getPrefixedGroupName(_sutName, 'startup and authentication:'), () {
    testWidgets('application should start up successfully', (final WidgetTester tester) async {
      await initTest(tester, login: false);

      // most basic test: root element should exist, i.e. app should be up and running successfully
      expectWidgetByKey("root", matchers.findsOneWidget);
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
      expectWidgetByKey('training_log_screen', matchers.findsOneWidget);
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

      // there should be exercise over view items corresponding to the domain model's muscle groups
      expectWidgetByType(ExerciseOverviewItem, matchers.findsWidgets);
    });

    testWidgets('receives mocked exercises for selected muscle group', (final WidgetTester tester) async {
      final mockExerciseService = MockExerciseService();
      when(mockExerciseService.getExercisesByMuscleGroup(any, any)).thenAnswer(
        (final _) async => const ServerResponse.success(mock_exercises.data),
      );

      await initTest(tester, login: true, additionalDependencyOverrides: (final Injector injector) {
        registerSingleton<ExerciseService, MockExerciseService>(injector, () => mockExerciseService);
      });

      // focus exercise screen
      await navigateToScreen('app_drawer.exercises', tester);
      expectWidgetByKey('exercises_screen', matchers.findsOneWidget);

      // there should be exercise over view items corresponding to the domain model's muscle groups
      expectWidgetByType(ExerciseOverviewItem, matchers.findsWidgets);

      // when tapping on of those muscle group items, the respective screen should open up with exercises returned by the mocked ExerciseService
      final firstOverviewItem = findByType(ExerciseOverviewItem).first;
      await tap(firstOverviewItem, tester);

      expectWidgetByType(ExerciseByMuscleGroupItem, matchers.findsWidgets);
    });
  });
}
