import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/details/exercise_history_page.dart';
import 'package:client/widgets/exercises/details/exercise_information_page.dart';
import 'package:client/widgets/exercises/details/exercise_statistics_page.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_detail_screen');

class ExerciseDetailScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const ExerciseDetailScreen(this._exercise, {final Key? key}) : super(key: key);

  static const routeName = '/exercise-details';
  final Exercise? _exercise;

  Widget _buildFallbackScreen(final StringLocalizations uiStrings) {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: Text(uiStrings.exerciseDetailScreen_fallback_appBar_title),
      ),
      body: Center(
        child: Text(uiStrings.exerciseDetailScreen_fallback_body_text),
      ),
    );
  }

  Widget _buildScreen(final BuildContext context, final StringLocalizations uiStrings, final Exercise exercise) {
    _logger.v(prepare('_buildScreen()'));
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          bottom: TabBar(
            tabs: [
              Tab(
                text: uiStrings.exerciseDetailScreen_tab_info,
                icon: const Icon(Icons.info),
              ),
              Tab(
                key: const Key('exercise_detail_screen.history'),
                text: uiStrings.exerciseDetailScreen_tab_history,
                icon: const Icon(Icons.history),
              ),
              Tab(
                text: uiStrings.exerciseDetailScreen_tab_statistics,
                icon: const Icon(Icons.insights),
              ),
            ],
          ),
          title: Text(exercise.name),
        ),
        body: TabBarView(
          children: [
            ExerciseInformation(exercise),
            ExerciseHistoryPage(exercise),
            ExerciseStatistics(exercise),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return _exercise == null ? _buildFallbackScreen(uiStrings) : _buildScreen(context, uiStrings, _exercise!);
  }
}
