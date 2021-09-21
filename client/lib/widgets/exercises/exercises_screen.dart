import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise_tag.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:client/widgets/exercises/exercises_by_tag_screen.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercises_screen');

class ExercisesScreen extends StatelessWidget with LogMessagePreparer {
  const ExercisesScreen({final Key? key}) : super(key: key);

  static const routeName = '/exercises';

  Future<void> _fetchExerciseTags(final BuildContext context) async {
    _logger.v(prepare('_fetchExerciseTags()'));
    await Provider.of<ExerciseProvider>(context, listen: false).fetchTags();
  }

  Widget _buildTagList(final BuildContext context) {
    _logger.v(prepare('_buildTagList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchExerciseTags(context),
        builder: (final _, final snapshot) => snapshot.waitSwitch(
          RefreshIndicator(
            onRefresh: () => _fetchExerciseTags(context),
            child: Consumer<ExerciseProvider>(
              builder: (final _, final exerciseData, final __) {
                _logger.v(prepare('_buildTagList.Consumer.builder()'));
                return Scrollbar(
                  isAlwaysShown: true,
                  child: ListView.builder(
                    itemCount: exerciseData.exerciseTags.length,
                    itemBuilder: (final _, final index) {
                      final exerciseTag = exerciseData.exerciseTags[index];
                      return Column(
                        children: [
                          _ExerciseOverviewItem(exerciseTag),
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
            'Exercises by Tags',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const Spacer(),
          IconButton(
            tooltip: 'Search Tags',
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
          _buildTagList(context),
        ],
      ),
    );
  }
}

class _ExerciseOverviewItem extends StatelessWidget with LogMessagePreparer {
  const _ExerciseOverviewItem(this._exerciseTag, {final Key? key}) : super(key: key);

  final ExerciseTag _exerciseTag;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return ListTile(
      title: Text(_exerciseTag.name),
      leading: const CircleAvatar(
        backgroundColor: Colors.transparent,
        foregroundImage: AssetImage('assets/images/flutter_logo.png'),
      ),
      onTap: () {
        Navigator.of(context).pushNamed(ExercisesByTagScreen.routeName, arguments: _exerciseTag);
      },
    );
  }
}
