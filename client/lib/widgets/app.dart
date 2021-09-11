import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/programs/programs_screen.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/statistics/statistics_screen.dart';
import 'package:client/widgets/workout_overview/workout_overview_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

/// The Widget that configures your application.
class MyApp extends StatelessWidget {
  static const _restorationScopeId = 'com.witness.client';

  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      restorationScopeId: _restorationScopeId,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      onGenerateTitle: (BuildContext context) => AppLocalizations.of(context)!.appTitle,
      theme: ThemeData.light(),
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
