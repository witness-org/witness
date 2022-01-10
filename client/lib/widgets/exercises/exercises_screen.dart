import 'package:client/extensions/enum_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/image_provider_facade.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/editing/edit_exercise_screen.dart';
import 'package:client/widgets/exercises/exercises_by_muscle_group_screen.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:flutter/material.dart';
import 'package:injector/injector.dart';

final _logger = getLogger('exercises_screen');

class ExercisesScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const ExercisesScreen({
    final Future<void> Function(BuildContext context, Exercise selectedExercise)? selectExerciseAction,
    final Key? key = const Key('exercises_screen'),
  })  : _selectExerciseAction = selectExerciseAction,
        super(key: key);

  static const routeName = '/exercises';
  final Future<void> Function(BuildContext context, Exercise selectedExercise)? _selectExerciseAction;

  Widget _buildMuscleGroupList(final BuildContext context) {
    _logger.v(prepare('_buildMuscleGroupList()'));
    return Expanded(
      child: Scrollbar(
        isAlwaysShown: true,
        child: ListView.builder(
          scrollDirection: Axis.vertical,
          shrinkWrap: true,
          itemCount: MuscleGroup.values.length,
          itemBuilder: (final _, final index) {
            final muscleGroup = MuscleGroup.values[index];
            return Column(
              children: [
                ExerciseOverviewItem(muscleGroup, selectExerciseAction: _selectExerciseAction),
                const Divider(),
              ],
            );
          },
        ),
      ),
    );
  }

  Padding _buildHeader(final BuildContext context, final StringLocalizations uiStrings) {
    _logger.v(prepare('_buildHeader()'));
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, top: 10),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            uiStrings.exercisesScreen_header_text,
            style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const Spacer(),
          IconButton(
            tooltip: uiStrings.exercisesScreen_search_tooltip,
            onPressed: () {},
            icon: const Icon(Icons.search),
          ),
          IconButton(
            tooltip: uiStrings.exercisesScreen_createNew_tooltip,
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
    final uiStrings = getLocalizedStrings(context);
    return Scaffold(
      appBar: _selectExerciseAction == null
          ? const MainAppBar()
          : AppBar(
              title: Text(uiStrings.exercisesScreen_selectExercise_appBarText),
            ),
      drawer: _selectExerciseAction == null ? const AppDrawer() : null,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(context, uiStrings),
          const Divider(),
          _buildMuscleGroupList(context),
        ],
      ),
    );
  }
}

class ExerciseOverviewItem extends StatelessWidget with LogMessagePreparer {
  const ExerciseOverviewItem(
    this._muscleGroup, {
    final Future<void> Function(BuildContext context, Exercise selectedExercise)? selectExerciseAction,
    final Key? key,
  })  : _selectExerciseAction = selectExerciseAction,
        super(key: key);

  static final Injector _injector = Injector.appInstance;
  final MuscleGroup _muscleGroup;
  final Future<void> Function(BuildContext context, Exercise selectedExercise)? _selectExerciseAction;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final imageProvider = _injector.get<ImageProviderFacade>();
    return ListTile(
      title: Text(_muscleGroup.toUiString()),
      leading: CircleAvatar(
        backgroundColor: Colors.transparent,
        foregroundImage: imageProvider.fromAsset('assets/images/dumbbell.png'),
      ),
      onTap: () {
        Navigator.of(context).pushNamed(ExercisesByMuscleGroupScreen.routeName, arguments: [_muscleGroup, _selectExerciseAction]);
      },
    );
  }
}
