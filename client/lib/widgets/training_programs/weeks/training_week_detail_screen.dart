import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/training_programs/weeks/training_day_card.dart';
import 'package:client/widgets/training_programs/weeks/training_week_header.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('day_detail_screen');

class TrainingWeekDetailScreen extends StatelessWidget {
  static const routeName = '/training-week-details';
  final TrainingWeekOverview? _trainingWeek;
  final String? _trainingProgramName;

  const TrainingWeekDetailScreen(this._trainingWeek, this._trainingProgramName, {Key? key}) : super(key: key);

  Widget _buildFallbackScreen() {
    _logger.v('$runtimeType._buildFallbackScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text('No Training Week selected'),
      ),
    );
  }

  Future<void> _fetchTrainingDays(BuildContext context, int weekId) async {
    _logger.v('$runtimeType._fetchTrainingDays()');
    await Provider.of<TrainingProgramProvider>(context, listen: false).fetchTrainingDays(weekId);
  }

  Widget _buildWeekView(BuildContext context, TrainingWeekOverview week) {
    _logger.v('$runtimeType._buildWeekView()');
    return Expanded(
      child: FutureBuilder(
        future: _fetchTrainingDays(context, week.id),
        builder: (_, snapshot) => snapshot.waitSwitch(
          Consumer<TrainingProgramProvider>(
            builder: (_, providerData, __) => Scrollbar(
              isAlwaysShown: true,
              child: ListView.builder(
                itemCount: providerData.trainingDaysOfWeek(week.id).length,
                itemBuilder: (_, index) => Column(
                  children: [
                    TrainingDayCard(providerData.trainingDaysOfWeek(week.id)[index], week.number),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildScreen(BuildContext context, TrainingWeekOverview week, String programName) {
    _logger.v('$runtimeType._buildScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text('Week ${week.number}'),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Add Day to Training Program',
        child: Icon(Icons.add),
        onPressed: () {
          // TODO go to workout creation screen
        },
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(left: 16, top: 10, right: 16),
            child: TrainingWeekHeader(week, programName),
          ),
          _buildWeekView(context, week),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return _trainingWeek == null || _trainingProgramName == null
        ? _buildFallbackScreen()
        : _buildScreen(context, _trainingWeek!, _trainingProgramName!);
  }
}
