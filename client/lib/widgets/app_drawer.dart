import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/programs/programs_screen.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/statistics/statistics_screen.dart';
import 'package:client/widgets/workout_overview/workout_overview_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart';

class AppDrawer extends StatelessWidget {
  const AppDrawer({Key? key}) : super(key: key);

  Widget _buildListTile(String title, IconData icon, void Function() onTap) {
    return ListTile(
      leading: Icon(icon),
      title: Text(title),
      onTap: onTap,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: Column(
        children: [
          Container(
            height: 80,
            width: double.infinity,
            padding: EdgeInsets.only(left: 20, top: 30, right: 20, bottom: 0),
            alignment: Alignment.centerLeft,
            color: Theme.of(context).colorScheme.secondary,
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.appTitle,
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 26, color: Colors.white),
              ),
            ),
          ),
          //SizedBox(height: 20),
          _buildListTile('Workout Overview', Icons.date_range, () => Navigator.of(context).pushReplacementNamed(WorkoutOverviewScreen.routeName)),
          _buildListTile('Exercises', Icons.fitness_center, () => Navigator.of(context).pushReplacementNamed(ExercisesScreen.routeName)),
          _buildListTile('Programs', Icons.article_outlined, () => Navigator.of(context).pushReplacementNamed(ProgramsScreen.routeName)),
          _buildListTile('Statistics', Icons.insights, () => Navigator.of(context).pushReplacementNamed(StatisticsScreen.routeName)),
          _buildListTile('Settings', Icons.settings, () => Navigator.of(context).pushReplacementNamed(SettingsScreen.routeName)),
        ],
      ),
    );
  }
}
