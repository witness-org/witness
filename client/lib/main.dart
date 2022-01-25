import 'package:client/app_arguments.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/services/exercise_service.dart';
import 'package:client/services/firebase_service.dart';
import 'package:client/services/training_program_service.dart';
import 'package:client/services/user_service.dart';
import 'package:client/widgets/app.dart';
import 'package:client/widgets/common/image_provider_facade.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:injector/injector.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

Future<void> main() async => bootstrap();

Future<void> bootstrap([final AppArguments arguments = const AppArguments.empty()]) async {
  final injector = Injector.appInstance
    ..clearAll()
    ..registerSingleton<ExerciseService>(() => ExerciseService())
    ..registerSingleton<FirebaseService>(() => FirebaseService())
    ..registerSingleton<TrainingProgramService>(() => TrainingProgramService())
    ..registerSingleton<UserService>(() => UserService())
    ..registerSingleton<ImageProviderFacade>(() => ImageProviderFacade())
    ..registerSingleton<Future<FirebaseAuth>>(() async {
      await Firebase.initializeApp();
      return FirebaseAuth.instance;
    });
  arguments.overrideDependencies(injector);

  // Initialize the builder. This could be done anywhere, but must be done before the loader is first shown.
  // We use the default progress loader which offers a simple CircularProgressIndicator and animations when shown/dismissed.
  ProgressLoader().widgetBuilder = null;

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (final _) => AuthProvider()),
        ChangeNotifierProxyProvider<AuthProvider, ExerciseProvider>(
          create: (final _) => ExerciseProvider.empty(),
          update: (final _, final auth, final previousExercises) => ExerciseProvider.fromProviders(auth, previousExercises),
        ),
        ChangeNotifierProxyProvider<AuthProvider, TrainingProgramProvider>(
          create: (final _) => TrainingProgramProvider.empty(),
          update: (final _, final auth, final previousTrainingPrograms) => TrainingProgramProvider.fromProviders(auth, previousTrainingPrograms),
        ),
      ],
      child: const WitnessClient(),
    ),
  );
}
