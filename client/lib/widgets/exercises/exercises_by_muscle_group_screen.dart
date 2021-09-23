import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercises_by_muscle_group_screen');

class ExercisesByMuscleGroupScreen extends StatelessWidget with LogMessagePreparer {
  const ExercisesByMuscleGroupScreen(this._muscleGroup, {final Key? key}) : super(key: key);

  static const routeName = '/exercises-by-muscle-group';
  final MuscleGroup? _muscleGroup;

  Future<void> _fetchExercisesByMuscleGroup(final BuildContext context, final MuscleGroup group) async {
    _logger.v(prepare('_fetchExercisesByMuscleGroup()'));
    await Provider.of<ExerciseProvider>(context, listen: false).fetchExercisesByMuscleGroup(group);
  }

  Widget _buildFallbackScreen() {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: const Text('No muscle group selected'),
      ),
    );
  }

  Widget _buildHeader(final BuildContext context, final MuscleGroup group) {
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            // TODO(raffaelfoidl): fix UI when text too long
            'Muscle Group "${group.name}"',
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const Spacer(),
          IconButton(
            tooltip: 'Search Exercises',
            onPressed: () {},
            icon: const Icon(Icons.search),
          ),
          IconButton(
            tooltip: 'Create new Exercise',
            onPressed: () => Navigator.of(context).pushNamed(EditExerciseScreen.routeName),
            icon: const Icon(Icons.add),
          ),
        ],
      ),
    );
  }

  Widget _buildExerciseList(final BuildContext context, final MuscleGroup group) {
    _logger.v(prepare('_buildExerciseList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchExercisesByMuscleGroup(context, group),
        builder: (final _, final snapshot) {
          return snapshot.waitSwitch(
            RefreshIndicator(
              onRefresh: () => _fetchExercisesByMuscleGroup(context, group),
              child: Consumer<ExerciseProvider>(
                builder: (final _, final exerciseData, final __) {
                  _logger.v(prepare('_buildExerciseList.Consumer.builder()'));
                  return Scrollbar(
                    isAlwaysShown: true,
                    child: ListView.builder(
                      itemCount: exerciseData.getExercisesByMuscleGroup(group).length,
                      itemBuilder: (final _, final index) {
                        final exercise = exerciseData.getExercisesByMuscleGroup(group)[index];
                        return Column(
                          children: [
                            _ExerciseByMuscleGroupItem(exercise),
                            const Divider(),
                          ],
                        );
                      },
                    ),
                  );
                },
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildScreen(final BuildContext context, final MuscleGroup group) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: Text(group.name),
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(context, group),
          _buildExerciseList(context, group),
        ],
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    return _muscleGroup == null ? _buildFallbackScreen() : _buildScreen(context, _muscleGroup!);
  }
}

class _ExerciseByMuscleGroupItem extends StatelessWidget with LogMessagePreparer {
  const _ExerciseByMuscleGroupItem(this._exercise, {final Key? key}) : super(key: key);

  final Exercise _exercise;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return ListTile(
      title: Text(_exercise.title),
      leading: const CircleAvatar(
        backgroundColor: Colors.transparent,
        foregroundImage: AssetImage('assets/images/flutter_logo.png'),
      ),
      onTap: () {
        Navigator.of(context).pushNamed(ExerciseDetailScreen.routeName, arguments: _exercise);
      },
    );
  }
}
