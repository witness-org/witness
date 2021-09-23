import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/widgets/exercises/details/exercise_history.dart';
import 'package:client/widgets/exercises/details/exercise_information.dart';
import 'package:client/widgets/exercises/details/exercise_statistics.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_detail_screen');

class ExerciseDetailScreen extends StatelessWidget with LogMessagePreparer {
  const ExerciseDetailScreen(this._exercise, {final Key? key}) : super(key: key);

  static const routeName = '/exercise-details';
  final Exercise? _exercise;

  Widget _buildFallbackScreen() {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: const Text('Exercise Details'),
      ),
      body: const Center(
        child: Text('No exercise selected'),
      ),
    );
  }

  Widget _buildScreen(final BuildContext context, final Exercise exercise) {
    _logger.v(prepare('_buildScreen()'));
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          bottom: const TabBar(
            tabs: [
              Tab(
                text: 'Info',
                icon: Icon(Icons.info),
              ),
              Tab(
                text: 'History',
                icon: Icon(Icons.history),
              ),
              Tab(
                text: 'Statistics',
                icon: Icon(Icons.insights),
              ),
            ],
          ),
          title: Text(exercise.title),
        ),
        body: TabBarView(
          children: [
            ExerciseInformation(exercise),
            ExerciseHistory(exercise),
            ExerciseStatistics(exercise),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return _exercise == null ? _buildFallbackScreen() : _buildScreen(context, _exercise!);
  }
}
