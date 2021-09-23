import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/training_programs/training_programs/training_program_header.dart';
import 'package:client/widgets/training_programs/training_programs/training_week_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('training_program_detail_screen');

class TrainingProgramDetailScreen extends StatelessWidget with LogMessagePreparer {
  const TrainingProgramDetailScreen(this._program, {final Key? key}) : super(key: key);

  static const routeName = '/training-program-details';
  final TrainingProgramOverview? _program;

  Widget _buildFallbackScreen() {
    _logger.v(prepare('_buildFallbackScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: const Text('No Training Program selected'),
      ),
    );
  }

  Future<void> _fetchTrainingWeeks(final BuildContext context, final int programId) async {
    _logger.v(prepare('_fetchTrainingWeeks()'));
    await Provider.of<TrainingProgramProvider>(context, listen: false).fetchTrainingWeeks(programId);
  }

  Widget _buildScreen(final BuildContext context, final TrainingProgramOverview program) {
    _logger.v(prepare('_buildScreen()'));
    return Scaffold(
      appBar: AppBar(
        title: Text(program.name),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Add Week to Program',
        child: const Icon(Icons.add),
        onPressed: () {
          // TODO(raffaelfoidl-leabrugger): Go to week creation screen
        },
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(padding: const EdgeInsets.only(left: 16, top: 10, right: 16), child: TrainingProgramHeader(program)),
          Expanded(
            child: FutureBuilder<void>(
              future: _fetchTrainingWeeks(context, program.id),
              builder: (final _, final snapshot) => snapshot.waitSwitch(
                Consumer<TrainingProgramProvider>(
                  builder: (final _, final providerData, final __) => Scrollbar(
                    isAlwaysShown: true,
                    child: ListView.builder(
                      itemCount: providerData.trainingWeeksOfProgram(program.id).length,
                      itemBuilder: (final _, final index) => TrainingWeekCard(providerData.trainingWeeksOfProgram(program.id)[index], program.name),
                    ),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return _program == null ? _buildFallbackScreen() : _buildScreen(context, _program!);
  }
}
