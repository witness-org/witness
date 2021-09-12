import 'package:client/providers/auth_provider.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'widgets/app.dart';

void main() async {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AuthProvider()),
        ChangeNotifierProxyProvider<AuthProvider, ExerciseProvider>(
          create: (_) => ExerciseProvider.empty(),
          update: (ctx, auth, previousExercises) => ExerciseProvider.fromProviders(auth, previousExercises),
        ),
      ],
      child: WitnessClient(
        key: ValueKey("root"),
      ),
    ),
  );
}
