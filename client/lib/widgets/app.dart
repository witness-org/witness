import 'package:client/extensions/cast_extensions.dart';
import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_tag.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:client/widgets/exercises/exercises_by_tag_screen.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/statistics/statistics_screen.dart';
import 'package:client/widgets/training_programs/days/training_day_detail_screen.dart';
import 'package:client/widgets/training_programs/training_programs/training_program_detail_screen.dart';
import 'package:client/widgets/training_programs/training_programs_overview_screen.dart';
import 'package:client/widgets/training_programs/weeks/training_week_detail_screen.dart';
import 'package:client/widgets/workouts/workout_overview_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

final _logger = getLogger('app');

class WitnessClient extends StatefulWidget {
  const WitnessClient({Key? key}) : super(key: key);

  static ColorScheme _getLightColorTheme(MaterialColor primaryColor, MaterialColor secondaryColor) {
    final primarySwatch = primaryColor;
    final accentColor = secondaryColor;

    final bool primaryIsDark = ThemeData.estimateBrightnessForColor(primarySwatch) == Brightness.dark;
    final bool secondaryIsDark = ThemeData.estimateBrightnessForColor(accentColor) == Brightness.dark;

    return ColorScheme(
      primary: primarySwatch,
      primaryVariant: primarySwatch.shade700,
      secondary: accentColor,
      secondaryVariant: accentColor.shade700,
      surface: Colors.white,
      background: primarySwatch.shade200,
      error: Colors.red.shade700,
      onPrimary: primaryIsDark ? Colors.white : Colors.black,
      onSecondary: secondaryIsDark ? Colors.white : Colors.black,
      onSurface: Colors.black,
      onBackground: primaryIsDark ? Colors.white : Colors.black,
      onError: Colors.white,
      brightness: Brightness.light,
    );
  }

  @override
  State<WitnessClient> createState() => _WitnessClientState();
}

class _WitnessClientState extends State<WitnessClient> {
  final ColorScheme colorScheme = WitnessClient._getLightColorTheme(Colors.purple, Colors.amber);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return MaterialApp(
      // TODO: think of naming convention for keys app_en.arb that makes life easy for us
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      onGenerateTitle: (BuildContext context) => AppLocalizations.of(context)!.appTitle,
      theme: ThemeData(
        primarySwatch: Colors.purple,
        colorScheme: colorScheme,
      ),
      // TODO maybe define dark theme analogously to light theme
      darkTheme: ThemeData.dark(),
      themeMode: ThemeMode.system,
      onGenerateRoute: (RouteSettings routeSettings) {
        return MaterialPageRoute<void>(
          settings: routeSettings,
          builder: (_) {
            switch (routeSettings.name) {
              // home
              case '/':
                return WorkoutOverviewScreen(routeSettings.arguments.castOrFallback<DateTime>(DateTime.now().dateOnly()));

              // workouts
              case WorkoutOverviewScreen.routeName:
                return WorkoutOverviewScreen(routeSettings.arguments.castOrFallback<DateTime>(DateTime.now().dateOnly()));
              case ExercisesScreen.routeName:
                return ExercisesScreen();

              // exercises
              case ExercisesByTagScreen.routeName:
                return ExercisesByTagScreen(routeSettings.arguments.castOrNull<ExerciseTag>());
              case ExerciseDetailScreen.routeName:
                return ExerciseDetailScreen(routeSettings.arguments.castOrNull<Exercise>());
              case EditExerciseScreen.routeName:
                return EditExerciseScreen(routeSettings.arguments.castOrNull<Exercise>());

              // training programs
              case TrainingProgramsOverviewScreen.routeName:
                return TrainingProgramsOverviewScreen();
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
                return StatisticsScreen();

              // settings
              case SettingsScreen.routeName:
                return SettingsScreen();

              // fallback
              default:
                throw Exception("Unknown route \"${routeSettings.name}\".");
            }
          },
        );
      },
    );
  }
}
