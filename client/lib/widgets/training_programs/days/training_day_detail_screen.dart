import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/models/training_programs/workout.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/days/training_day_header.dart';
import 'package:client/widgets/training_programs/days/workout_expander_view.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('training_day_detail_screen');

class TrainingDayDetailScreen extends StatefulWidget {
  const TrainingDayDetailScreen(this._day, this._weekNumber, {final Key? key}) : super(key: key);

  static const routeName = '/training-day-details';
  final TrainingDayOverview? _day;
  final int? _weekNumber;

  @override
  State<TrainingDayDetailScreen> createState() => _TrainingDayDetailScreenState();
}

class _TrainingDayDetailScreenState extends State<TrainingDayDetailScreen> with LogMessagePreparer, StringLocalizer {
  List<Workout>? _items;

  bool get hasInputData {
    return widget._day != null && widget._weekNumber != null;
  }

  @override
  void initState() {
    super.initState();
    _fetchWorkouts(context);
  }

  Future<void> _fetchWorkouts(final BuildContext context) async {
    _logger.v(prepare('_fetchWorkouts()'));
    if (!hasInputData) {
      return;
    }

    final provider = Provider.of<TrainingProgramProvider>(context, listen: false);
    provider.fetchWorkouts(widget._day!.id).then((final value) {
      setState(() => _items = provider.workoutsOfDay(widget._day!.id));
    });
  }

  Widget _buildFallbackScreen(final StringLocalizations uiStrings) {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: Text(uiStrings.trainingDayDetailScreen_fallback_appBar_title),
      ),
    );
  }

  Widget _buildBody(final TrainingDayOverview day, final List<Workout> items) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 16, top: 10, right: 16),
          child: TrainingDayHeader(day),
        ),
        WorkoutExpanderView(items),
      ],
    );
  }

  Widget _buildScreen(final BuildContext context, final StringLocalizations uiStrings, final TrainingDayOverview day, final int weekNumber) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: Text('${uiStrings.trainingDayDetailScreen_appBar_weekNumber_prefix} $weekNumber - '
            '${uiStrings.trainingDayDetailScreen_appBar_dayNumber_prefix} ${day.number}'),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: uiStrings.trainingDayDetailScreen_action_addExercise,
        child: const Icon(Icons.add),
        onPressed: () {
          // TODO(raffaelfoidl-leabrugger): select workout and exercise
        },
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      body: _items == null
          ? const Center(
              child: CircularProgressIndicator(),
            )
          : _buildBody(day, _items!),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return hasInputData ? _buildScreen(context, uiStrings, widget._day!, widget._weekNumber!) : _buildFallbackScreen(uiStrings);
  }
}
