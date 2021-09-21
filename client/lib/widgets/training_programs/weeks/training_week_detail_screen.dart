import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/training_programs/weeks/training_day_card.dart';
import 'package:client/widgets/training_programs/weeks/training_week_header.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('day_detail_screen');

class TrainingWeekDetailScreen extends StatelessWidget with LogMessagePreparer {
  const TrainingWeekDetailScreen(this._trainingWeek, this._trainingProgramName, {final Key? key}) : super(key: key);

  static const routeName = '/training-week-details';
  final TrainingWeekOverview? _trainingWeek;
  final String? _trainingProgramName;

  Widget _buildFallbackScreen() {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: const Text('No Training Week selected'),
      ),
    );
  }

  Future<void> _fetchTrainingDays(final BuildContext context, final int weekId) async {
    _logger.v(prepare('_fetchTrainingDays()'));
    await Provider.of<TrainingProgramProvider>(context, listen: false).fetchTrainingDays(weekId);
  }

  Widget _buildWeekView(final BuildContext context, final TrainingWeekOverview week) {
    _logger.v(prepare('_buildWeekView()'));
    return Expanded(
      child: FutureBuilder(
        future: _fetchTrainingDays(context, week.id),
        builder: (final _, final snapshot) => snapshot.waitSwitch(
          Consumer<TrainingProgramProvider>(
            builder: (final _, final providerData, final __) => Scrollbar(
              isAlwaysShown: true,
              child: ListView.builder(
                itemCount: providerData.trainingDaysOfWeek(week.id).length,
                itemBuilder: (final _, final index) => Column(
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

  Widget _buildScreen(final BuildContext context, final TrainingWeekOverview week, final String programName) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: Text('Week ${week.number}'),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Add Day to Training Program',
        child: const Icon(Icons.add),
        onPressed: () {
          // TODO(raffaelfoidl-leabrugger): Go to day/workout creation screen
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
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return _trainingWeek == null || _trainingProgramName == null
        ? _buildFallbackScreen()
        : _buildScreen(context, _trainingWeek!, _trainingProgramName!);
  }
}
