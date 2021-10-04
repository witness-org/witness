import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/exercises_screen.dart';
import 'package:client/widgets/settings/settings_screen.dart';
import 'package:client/widgets/statistics/statistics_screen.dart';
import 'package:client/widgets/training_logs/training_log_screen.dart';
import 'package:client/widgets/training_programs/training_programs_overview_screen.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('app_drawer');

class AppDrawer extends StatelessWidget with LogMessagePreparer, StringLocalizer {
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
    final uiStrings = getLocalizedStrings(context);
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
                uiStrings.appTitle,
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 26, color: theme.colorScheme.onPrimary),
              ),
            ),
          ),
          _buildListTile(
            uiStrings.appDrawer_tile_trainingLog,
            Icons.date_range,
            () => Navigator.of(context).pushReplacementNamed(TrainingLogScreen.routeName),
          ),
          _buildListTile(
            uiStrings.appDrawer_tile_exercises,
            Icons.fitness_center,
            () => Navigator.of(context).pushReplacementNamed(ExercisesScreen.routeName),
          ),
          _buildListTile(
            uiStrings.appDrawer_tile_programs,
            Icons.article_outlined,
            () => Navigator.of(context).pushReplacementNamed(TrainingProgramsOverviewScreen.routeName),
          ),
          _buildListTile(
            uiStrings.appDrawer_tile_statistics,
            Icons.insights,
            () => Navigator.of(context).pushReplacementNamed(StatisticsScreen.routeName),
          ),
          _buildListTile(
            uiStrings.appDrawer_tile_settings,
            Icons.settings,
            () => Navigator.of(context).pushReplacementNamed(SettingsScreen.routeName),
          ),
        ],
      ),
    );
  }
}
