import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/models/training_programs/workout.dart';
import 'package:client/providers/training_program_provider.dart';
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

class _TrainingDayDetailScreenState extends State<TrainingDayDetailScreen> with LogMessagePreparer {
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

  Widget _buildFallbackScreen() {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: const Text('No Day selected'),
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

  Widget _buildScreen(final BuildContext context, final TrainingDayOverview day, final int weekNumber) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: Text('Week $weekNumber - Day ${day.number}'),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Add Exercise to Workout',
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
    return hasInputData ? _buildScreen(context, widget._day!, widget._weekNumber!) : _buildFallbackScreen();
  }
}
