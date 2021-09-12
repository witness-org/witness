import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercise_tag.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:client/widgets/exercises/exercises_by_tag_screen.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercises_screen');

class ExercisesScreen extends StatelessWidget {
  static const routeName = '/exercises';

  const ExercisesScreen({Key? key}) : super(key: key);

  Future<void> _fetchExerciseTags(BuildContext context) async {
    _logger.v('$runtimeType._fetchExerciseTags()');
    await Provider.of<ExerciseProvider>(context, listen: false).fetchTags();
  }

  Widget _buildTagList(BuildContext context) {
    _logger.v('$runtimeType._buildTagList()');
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchExerciseTags(context),
        builder: (_, snapshot) => snapshot.waitSwitch(
          RefreshIndicator(
            onRefresh: () => _fetchExerciseTags(context),
            child: Consumer<ExerciseProvider>(
              builder: (_, exerciseData, __) {
                _logger.v('$runtimeType._buildTagList.Consumer.builder()');
                return Scrollbar(
                  isAlwaysShown: true,
                  child: ListView.builder(
                    itemCount: exerciseData.exerciseTags.length,
                    itemBuilder: (_, index) {
                      // TODO caching of tags (and exercises per tag) in ExerciseProvider
                      final exerciseTag = exerciseData.exerciseTags[index];
                      return Column(
                        children: [
                          _ExerciseOverviewItem(exerciseTag),
                          Divider(),
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

  Padding _buildHeader(BuildContext context) {
    _logger.v('$runtimeType._buildHeader()');
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            'Exercises by Tags',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          Spacer(),
          IconButton(
            tooltip: 'Search Tags',
            onPressed: () {},
            icon: Icon(Icons.search),
          ),
          IconButton(
            tooltip: 'Create new Exercise',
            onPressed: () => Navigator.of(context).pushNamed(EditExerciseScreen.routeName),
            icon: Icon(Icons.add),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return Scaffold(
      appBar: MainAppBar(),
      drawer: AppDrawer(),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(context),
          Divider(),
          _buildTagList(context),
        ],
      ),
    );
  }
}

class _ExerciseOverviewItem extends StatelessWidget {
  final ExerciseTag _exerciseTag;

  const _ExerciseOverviewItem(this._exerciseTag, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
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
