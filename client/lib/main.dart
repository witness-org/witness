import 'package:client/providers/auth_provider.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:flutter/material.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

import 'widgets/app.dart';

void main() async {
  // Initialize the builder. This could be done anywhere, but must be done before the loader is first shown.
  // We use the default progress loader which offers a simple CircularProgressIndicator and animations when shown/dismissed.
  ProgressLoader().widgetBuilder = null;

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider()),
        ChangeNotifierProxyProvider<AuthProvider, ExerciseProvider>(
          create: (_) => ExerciseProvider.empty(),
          update: (_, auth, previousExercises) => ExerciseProvider.fromProviders(auth, previousExercises),
        ),
        ChangeNotifierProxyProvider<AuthProvider, TrainingProgramProvider>(
          create: (_) => TrainingProgramProvider.empty(),
          update: (_, auth, previousTrainingPrograms) => TrainingProgramProvider.fromProviders(auth, previousTrainingPrograms),
        )
      ],
      child: WitnessClient(
        key: ValueKey("root"),
      ),
    ),
  );
}
