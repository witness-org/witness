import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/extensions/cast_extensions.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/widgets/authentication/error_screen.dart';
import 'package:client/widgets/authentication/login_screen.dart';
import 'package:client/widgets/authentication/splash_screen.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:client/widgets/exercises/exercises_by_muscle_group_screen.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/statistics/statistics_screen.dart';
import 'package:client/widgets/training_programs/days/training_day_detail_screen.dart';
import 'package:client/widgets/training_programs/training_programs/training_program_detail_screen.dart';
import 'package:client/widgets/training_programs/training_programs_overview_screen.dart';
import 'package:client/widgets/training_programs/weeks/training_week_detail_screen.dart';
import 'package:client/widgets/workouts/workout_log_screen.dart';
import 'package:flutter/material.dart';
import 'package:timezone/timezone.dart';
import 'package:timezone/timezone.dart' as tz;

Route<dynamic>? selectRoute(final RouteSettings routeSettings, final AuthProvider auth) {
  return MaterialPageRoute<void>(
    settings: routeSettings,
    builder: (final _) {
      switch (routeSettings.name) {
        // home
        case '/':
          return auth.isAuthenticated
              ? WorkoutLogScreen(TZDateTime.now(tz.local))
              : FutureBuilder(
                  future: auth.reloadAuthentication(),
                  builder: (final ctx, final snapshot) => snapshot.waitSwitch(
                    const LoginScreen(),
                    waitingWidget: const SplashScreen(),
                    errorWidget: (final error) => ErrorScreen(errorText: error?.toString()),
                  ),
                );

        // workout logs
        case WorkoutLogScreen.routeName:
          return WorkoutLogScreen(routeSettings.arguments.castOrFallback<TZDateTime>(TZDateTime.now(tz.local)));

        // exercises
        case ExercisesScreen.routeName:
          return ExercisesScreen(
            selectExerciseAction: routeSettings.arguments.castOrNull<Future<void> Function(BuildContext context, Exercise selectedExercise)>(),
          );
        case ExercisesByMuscleGroupScreen.routeName:
          final args = routeSettings.arguments.castOrNull<List<Object?>>();
          return ExercisesByMuscleGroupScreen(
            args?[0].castOrNull<MuscleGroup>(),
            selectExerciseAction: args?[1].castOrNull<Future<void> Function(BuildContext context, Exercise selectedExercise)>(),
          );
        case ExerciseDetailScreen.routeName:
          return ExerciseDetailScreen(routeSettings.arguments.castOrNull<Exercise>());
        case EditExerciseScreen.routeName:
          return EditExerciseScreen(routeSettings.arguments.castOrNull<Exercise>());

        // training programs
        case TrainingProgramsOverviewScreen.routeName:
          return const TrainingProgramsOverviewScreen();
        case TrainingProgramDetailScreen.routeName:
          return TrainingProgramDetailScreen(routeSettings.arguments.castOrNull<TrainingProgramOverview>());
        case TrainingDayDetailScreen.routeName:
          final args = routeSettings.arguments.castOrNull<List<Object>>();
          return TrainingDayDetailScreen(args?[0].castOrNull<TrainingDayOverview>(), args?[1].castOrNull<int>());
        case TrainingWeekDetailScreen.routeName:
          final args = routeSettings.arguments.castOrNull<List<Object>>();
          return TrainingWeekDetailScreen(args?[0].castOrNull<TrainingWeekOverview>(), args?[1].castOrNull<String>());

        // statistics
        case StatisticsScreen.routeName:
          return const StatisticsScreen();

        // settings
        case SettingsScreen.routeName:
          return const SettingsScreen();

        // fallback
        default:
          throw Exception('Unknown route "${routeSettings.name}".');
      }
    },
  );
}
