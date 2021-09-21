import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_tag.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('exercises_by_tag_screen');

class ExercisesByTagScreen extends StatelessWidget with LogMessagePreparer {
  const ExercisesByTagScreen(this._tag, {final Key? key}) : super(key: key);

  static const routeName = '/exercises-by-tag';
  final ExerciseTag? _tag;

  Future<void> _fetchExercisesByTag(final BuildContext context, final ExerciseTag tag) async {
    _logger.v(prepare('_fetchExercisesByTag()'));
    await Provider.of<ExerciseProvider>(context, listen: false).fetchExercisesByTag(tag);
  }

  Widget _buildFallbackScreen() {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: const Text('No Tag selected'),
      ),
    );
  }

  Widget _buildHeader(final BuildContext context, final ExerciseTag tag) {
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            // TODO(raffaelfoidl): fix UI when text too long
            'Exercises with Tag "${tag.name}"',
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

  Widget _buildExerciseList(final BuildContext context, final ExerciseTag tag) {
    _logger.v(prepare('_buildExerciseList()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchExercisesByTag(context, tag),
        builder: (final _, final snapshot) {
          return snapshot.waitSwitch(
            RefreshIndicator(
              onRefresh: () => _fetchExercisesByTag(context, tag),
              child: Consumer<ExerciseProvider>(
                builder: (final _, final exerciseData, final __) {
                  _logger.v(prepare('_buildExerciseList.Consumer.builder()'));
                  return Scrollbar(
                    isAlwaysShown: true,
                    child: ListView.builder(
                      itemCount: exerciseData.getExercisesByTags(tag).length,
                      itemBuilder: (final _, final index) {
                        final exercise = exerciseData.getExercisesByTags(tag)[index];
                        return Column(
                          children: [
                            _ExerciseByTagItem(exercise),
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

  Widget _buildScreen(final BuildContext context, final ExerciseTag tag) {
    _logger.v(prepare('_buildScreen()'));
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
  Widget build(final BuildContext context) {
    return _tag == null ? _buildFallbackScreen() : _buildScreen(context, _tag!);
  }
}

class _ExerciseByTagItem extends StatelessWidget with LogMessagePreparer {
  const _ExerciseByTagItem(this._exercise, {final Key? key}) : super(key: key);

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
