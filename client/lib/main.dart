import 'package:client/providers/auth_provider.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/providers/greeting_provider.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/app.dart';
import 'package:flutter/material.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

Future<void> main() async {
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
        ChangeNotifierProxyProvider<AuthProvider, GreetingProvider>(
          create: (final _) => GreetingProvider.empty(),
          update: (final _, final auth, final __) => GreetingProvider.fromProviders(auth),
        ),
      ],
      child: const WitnessClient(
        key: ValueKey("root"),
      ),
    ),
  );
}
