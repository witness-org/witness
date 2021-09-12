import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercise.dart';
import 'package:client/models/exercise_tag.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercises_by_tag_screen');

class ExercisesByTagScreen extends StatelessWidget {
  static const routeName = '/exercises-by-tag';
  final ExerciseTag? _tag;

  const ExercisesByTagScreen(this._tag, {Key? key}) : super(key: key);

  Future<void> _fetchExercisesByTag(BuildContext context, ExerciseTag tag) async {
    _logger.v('$runtimeType._fetchExercisesByTag()');
    await Provider.of<ExerciseProvider>(context, listen: false).fetchExercisesByTag(tag);
  }

  Widget _buildFallbackScreen() {
    _logger.v('$runtimeType._buildFallbackScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text('No Tag selected'),
      ),
    );
  }

  Widget _buildHeader(BuildContext context, ExerciseTag tag) {
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            // TODO fix UI when text too long
            'Exercises with Tag \"${tag.name}\"',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          Spacer(),
          IconButton(
            tooltip: 'Search Exercises',
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

  Widget _buildExerciseList(BuildContext context, ExerciseTag tag) {
    _logger.v('$runtimeType._buildExerciseList()');
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchExercisesByTag(context, tag),
        builder: (_, snapshot) {
          return snapshot.waitSwitch(
            RefreshIndicator(
              onRefresh: () => _fetchExercisesByTag(context, tag),
              child: Consumer<ExerciseProvider>(
                builder: (_, exerciseData, __) {
                  _logger.v('$runtimeType._buildExerciseList.Consumer.builder()');
                  return Scrollbar(
                    isAlwaysShown: true,
                    child: ListView.builder(
                      itemCount: exerciseData.getExercisesByTags(tag).length,
                      itemBuilder: (_, index) {
                        final exercise = exerciseData.getExercisesByTags(tag)[index];
                        return Column(
                          children: [
                            _ExerciseByTagItem(exercise),
                            Divider(),
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

  Widget _buildScreen(BuildContext context, ExerciseTag tag) {
    _logger.v('$runtimeType._buildScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text(tag.name),
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(context, tag),
          _buildExerciseList(context, tag),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return _tag == null ? _buildFallbackScreen() : _buildScreen(context, _tag!);
  }
}

class _ExerciseByTagItem extends StatelessWidget {
  final Exercise _exercise;

  const _ExerciseByTagItem(this._exercise, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return ListTile(
      title: Text(_exercise.title),
      leading: const CircleAvatar(
        foregroundImage: AssetImage('assets/images/flutter_logo.png'),
      ),
      onTap: () {
        Navigator.of(context).pushNamed(ExerciseDetailScreen.routeName, arguments: _exercise);
      },
    );
  }
}
