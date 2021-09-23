import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:client/widgets/exercises/exercises_by_muscle_group_screen.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercises_screen');

class ExercisesScreen extends StatelessWidget with LogMessagePreparer {
  const ExercisesScreen({final Key? key}) : super(key: key);

  static const routeName = '/exercises';

  Future<void> _fetchMuscleGroups(final BuildContext context) async {
    _logger.v(prepare('_fetchMuscleGroups()'));
    await Provider.of<ExerciseProvider>(context, listen: false).fetchMuscleGroups();
  }

  Widget _buildMuscleGroupList(final BuildContext context) {
    _logger.v(prepare('_buildMuscleGroupList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchMuscleGroups(context),
        builder: (final _, final snapshot) => snapshot.waitSwitch(
          RefreshIndicator(
            onRefresh: () => _fetchMuscleGroups(context),
            child: Consumer<ExerciseProvider>(
              builder: (final _, final exerciseData, final __) {
                _logger.v(prepare('_buildMuscleGroupList.Consumer.builder()'));
                return Scrollbar(
                  isAlwaysShown: true,
                  child: ListView.builder(
                    itemCount: exerciseData.muscleGroups.length,
                    itemBuilder: (final _, final index) {
                      final muscleGroup = exerciseData.muscleGroups[index];
                      return Column(
                        children: [
                          _ExerciseOverviewItem(muscleGroup),
                          const Divider(),
                        ],
                      );
                    },
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }

  Padding _buildHeader(final BuildContext context) {
    _logger.v(prepare('_buildHeader()'));
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          const Text(
            'Exercises by Muscle Group',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const Spacer(),
          IconButton(
            tooltip: 'Search Muscle Groups',
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

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return Scaffold(
      appBar: const MainAppBar(),
      drawer: const AppDrawer(),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(context),
          const Divider(),
          _buildMuscleGroupList(context),
        ],
      ),
    );
  }
}

class _ExerciseOverviewItem extends StatelessWidget with LogMessagePreparer {
  const _ExerciseOverviewItem(this._muscleGroup, {final Key? key}) : super(key: key);

  final MuscleGroup _muscleGroup;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return ListTile(
      title: Text(_muscleGroup.name),
      leading: const CircleAvatar(
        backgroundColor: Colors.transparent,
        foregroundImage: AssetImage('assets/images/flutter_logo.png'),
      ),
      onTap: () {
        Navigator.of(context).pushNamed(ExercisesByMuscleGroupScreen.routeName, arguments: _muscleGroup);
      },
    );
  }
}
