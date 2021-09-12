import 'package:client/extensions/cast_extensions.dart';
import 'package:client/extensions/date_time_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercise.dart';
import 'package:client/models/exercise_tag.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:client/widgets/exercises/exercises_by_tag_screen.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/programs/programs_screen.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/statistics/statistics_screen.dart';
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
              case '/':
                return WorkoutOverviewScreen(routeSettings.arguments.castOrFallback<DateTime>(DateTime.now().dateOnly()));
              case WorkoutOverviewScreen.routeName:
                return WorkoutOverviewScreen(routeSettings.arguments.castOrFallback<DateTime>(DateTime.now().dateOnly()));
              case ExercisesScreen.routeName:
                return ExercisesScreen();
              case ExercisesByTagScreen.routeName:
                return ExercisesByTagScreen(routeSettings.arguments.castOrNull<ExerciseTag>());
              case ExerciseDetailScreen.routeName:
                return ExerciseDetailScreen(routeSettings.arguments.castOrNull<Exercise>());
              case EditExerciseScreen.routeName:
                return EditExerciseScreen(routeSettings.arguments.castOrNull<Exercise>());
              case ProgramsScreen.routeName:
                return ProgramsScreen();
              case StatisticsScreen.routeName:
                return StatisticsScreen();
              case SettingsScreen.routeName:
                return SettingsScreen();
              default:
                throw Exception("Unknown route \"${routeSettings.name}\".");
            }
          },
        );
      },
    );
  }
}
