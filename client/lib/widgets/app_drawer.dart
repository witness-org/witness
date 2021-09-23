import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/statistics/statistics_screen.dart';
import 'package:client/widgets/training_logs/training_log_screen.dart';
import 'package:client/widgets/training_programs/training_programs_overview_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_gen/gen_l10n/app_localizations.dart'; // ignore: depend_on_referenced_packages

final _logger = getLogger('app_drawer');

class AppDrawer extends StatelessWidget with LogMessagePreparer {
  const AppDrawer({final Key? key}) : super(key: key);

  Widget _buildListTile(final String title, final IconData icon, final void Function() onTap) {
    return ListTile(
      leading: Icon(icon),
      title: Text(title),
      onTap: onTap,
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final theme = Theme.of(context);
    return Drawer(
      child: Column(
        children: [
          Container(
            height: 80,
            width: double.infinity,
            padding: const EdgeInsets.only(left: 20, top: 30, right: 20, bottom: 0),
            alignment: Alignment.centerLeft,
            color: theme.colorScheme.secondary,
            child: Center(
              child: Text(
                AppLocalizations.of(context)!.appTitle,
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 26, color: theme.colorScheme.onPrimary),
              ),
            ),
          ),
          _buildListTile('Training Log', Icons.date_range, () => Navigator.of(context).pushReplacementNamed(TrainingLogScreen.routeName)),
          _buildListTile('Exercises', Icons.fitness_center, () => Navigator.of(context).pushReplacementNamed(ExercisesScreen.routeName)),
          _buildListTile(
              'Programs', Icons.article_outlined, () => Navigator.of(context).pushReplacementNamed(TrainingProgramsOverviewScreen.routeName)),
          _buildListTile('Statistics', Icons.insights, () => Navigator.of(context).pushReplacementNamed(StatisticsScreen.routeName)),
          _buildListTile('Settings', Icons.settings, () => Navigator.of(context).pushReplacementNamed(SettingsScreen.routeName)),
        ],
      ),
    );
  }
}
