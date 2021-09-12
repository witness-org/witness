import 'package:client/extensions/cast_extensions.dart';
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
import 'package:client/widgets/workout_overview/workout_overview_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

final _logger = getLogger('app');

class WitnessClient extends StatelessWidget {
  const WitnessClient({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return MaterialApp(
      // TODO: think of naming convention for keys app_en.arb that makes life easy for us
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      onGenerateTitle: (BuildContext context) => AppLocalizations.of(context)!.appTitle,
      // TODO define light theme (at least primary, secondary, onPrimary, onSecondary, title)
      theme: ThemeData.light(),
      // TODO maybe define dark theme analogously to light theme
      darkTheme: ThemeData.dark(),
      themeMode: ThemeMode.system,
      onGenerateRoute: (RouteSettings routeSettings) {
        return MaterialPageRoute<void>(
          settings: routeSettings,
          builder: (_) {
            switch (routeSettings.name) {
              case '/':
                return WorkoutOverviewScreen();
              case WorkoutOverviewScreen.routeName:
                return WorkoutOverviewScreen();
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
