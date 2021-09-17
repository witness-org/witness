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
  static const routeName = '/training-day-details';
  final TrainingDayOverview? _day;
  final int? _weekNumber;

  const TrainingDayDetailScreen(this._day, this._weekNumber, {Key? key}) : super(key: key);

  @override
  State<TrainingDayDetailScreen> createState() => _TrainingDayDetailScreenState();
}

class _TrainingDayDetailScreenState extends State<TrainingDayDetailScreen> {
  List<Workout>? _items;

  bool get hasInputData {
    return widget._day != null && widget._weekNumber != null;
  }

  @override
  void initState() {
    super.initState();
    _fetchWorkouts(context);
  }

  Future<void> _fetchWorkouts(BuildContext context) async {
    _logger.v('$runtimeType._fetchWorkouts()');
    if (!hasInputData) {
      return;
    }

    final provider = Provider.of<TrainingProgramProvider>(context, listen: false);
    provider.fetchWorkouts(widget._day!.id).then((value) {
      setState(() => _items = provider.workoutsOfDay(widget._day!.id));
    });
  }

  Widget _buildFallbackScreen() {
    _logger.v('$runtimeType._buildFallbackScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text('No Day selected'),
      ),
    );
  }

  Widget _buildBody(TrainingDayOverview day, List<Workout> items) {
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

  Widget _buildScreen(BuildContext context, TrainingDayOverview day, int weekNumber) {
    _logger.v('$runtimeType._buildScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text('Week $weekNumber - Day ${day.number}'),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Add Exercise to Workout',
        child: Icon(Icons.add),
        onPressed: () {
          // TODO select workout and exercise
        },
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      body: _items == null
          ? Center(
              child: CircularProgressIndicator(),
            )
          : _buildBody(day, _items!),
    );
  }

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return hasInputData ? _buildScreen(context, widget._day!, widget._weekNumber!) : _buildFallbackScreen();
  }
}
