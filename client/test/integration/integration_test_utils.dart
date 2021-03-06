import 'package:client/app_arguments.dart';
import 'package:client/main.dart' as app;
import 'package:client/services/server_response.dart';
import 'package:client/services/workout_log_service.dart';
import 'package:client/widgets/common/image_provider_facade.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_auth_mocks/firebase_auth_mocks.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:injector/injector.dart';
import 'package:mockito/mockito.dart';

import 'integration_test.mocks.dart';

late MockUser fakeUser;
late MockFirebaseAuth fakeAuthentication;

Future<void> initTest(
  final WidgetTester tester, {
  final bool login = true,
  final void Function(Injector injector) additionalDependencyOverrides = AppArguments.noOverrides,
}) async {
  app.bootstrap(
    AppArguments(
      overrideDependencies: (final injector) {
        defaultDependencyOverrides(injector);
        additionalDependencyOverrides(injector);
      },
    ),
  );
  await tester.pumpAndSettle();

  if (!login) {
    return;
  }

  await enterTextByKey('login_card.email', fakeUser.email!, tester);
  await enterTextByKey('login_card.password', 'somePassword', tester);
  await tapByKey('login_card.submit', tester);
}

void defaultDependencyOverrides(final Injector injector) {
  fakeUser = MockUser(isAnonymous: false, uid: 'bobsFakeUid', email: 'bob@example.com', displayName: 'bob.example');
  fakeAuthentication = MockFirebaseAuth(signedIn: false, mockUser: fakeUser);
  final mockWorkoutLogService = MockWorkoutLogService();
  when(mockWorkoutLogService.getWorkoutLogsByDay(any, any)).thenAnswer((final _) async => const ServerResponse.success([]));

  registerSingleton<Future<FirebaseAuth>, Future<MockFirebaseAuth>>(injector, () async => fakeAuthentication);
  registerSingleton<ImageProviderFacade, MockImageProviderFacade>(injector, () => MockImageProviderFacade());
  registerSingleton<WorkoutLogService, MockWorkoutLogService>(injector, () => mockWorkoutLogService);
}

void registerSingleton<T, U extends T>(final Injector injector, final U Function() register) {
  injector.registerSingleton<T>(register, override: true);
}

void registerDependency<T, U extends T>(final Injector injector, final U Function() register) {
  injector.registerDependency<T>(register, override: true);
}

Finder findByKey(final String key) {
  return find.byKey(Key(key));
}

Finder findByType(final Type type) {
  return find.byType(type);
}

Finder findByTypeAndText(final Type type, final String text) {
  return find.widgetWithText(type, text);
}

Finder findByTypeAndContainingText(final Type type, final Pattern pattern) {
  return find.ancestor(of: find.textContaining(pattern), matching: find.byType(type));
}

Future<void> enterTextByKey(final String key, final String text, final WidgetTester tester) async {
  final finder = findByKey(key);
  await tester.enterText(finder, text);
  await tester.pumpAndSettle();
}

Future<void> tapByKey(final String key, final WidgetTester tester) async {
  await tester.tap(findByKey(key));
  await tester.pumpAndSettle();
}

Future<void> tap(final Finder finder, final WidgetTester tester) async {
  await tester.tap(finder);
  await tester.pumpAndSettle();
}

Future<void> openDrawer(final WidgetTester tester) async {
  final app = findByType(MaterialApp);
  await tester.dragFrom(tester.getTopLeft(app), const Offset(300, 0));
  await tester.pumpAndSettle();
}

void expectWidgetByKey(final String key, final dynamic matcher) {
  expect(findByKey(key), matcher);
}

void expectWidgetByType(final Type type, final dynamic matcher) {
  expect(findByType(type), matcher);
}

void expectWidgetByTypeAndText(final Type type, final String text, final dynamic matcher) {
  expect(findByTypeAndText(type, text), matcher);
}

void expectWidgetByTypeAndContainingText(final Type type, final Pattern pattern, final dynamic matcher) {
  expect(findByTypeAndContainingText(type, pattern), matcher);
}
