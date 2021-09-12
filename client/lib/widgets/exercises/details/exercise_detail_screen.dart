import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercise.dart';
import 'package:client/widgets/exercises/details/exercise_history.dart';
import 'package:client/widgets/exercises/details/exercise_information.dart';
import 'package:client/widgets/exercises/details/exercise_statistics.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_detail_screen');

class ExerciseDetailScreen extends StatelessWidget {
  static const routeName = '/exercise-details';
  final Exercise? _exercise;

  const ExerciseDetailScreen(this._exercise, {Key? key}) : super(key: key);

  Widget _buildFallbackScreen() {
    _logger.v('$runtimeType._buildFallbackScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text('Exercise Details'),
      ),
      body: Center(
        child: Text('No exercise selected'),
      ),
    );
  }

  Widget _buildScreen(BuildContext context, Exercise exercise) {
    _logger.v('$runtimeType._buildScreen()');
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
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return _exercise == null ? _buildFallbackScreen() : _buildScreen(context, _exercise!);
  }
}
