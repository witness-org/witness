import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/training_programs/training_programs/training_program_header.dart';
import 'package:client/widgets/training_programs/training_programs/training_week_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('training_program_detail_screen');

class TrainingProgramDetailScreen extends StatelessWidget {
  static const routeName = '/training-program-details';
  final TrainingProgramOverview? _program;

  const TrainingProgramDetailScreen(this._program, {Key? key}) : super(key: key);

  Widget _buildFallbackScreen() {
    _logger.v('$runtimeType._buildFallbackScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text('No Training Program selected'),
      ),
    );
  }

  Future<void> _fetchTrainingWeeks(BuildContext context, int programId) async {
    _logger.v('$runtimeType._fetchTrainingWeeks()');
    await Provider.of<TrainingProgramProvider>(context, listen: false).fetchTrainingWeeks(programId);
  }

  Widget _buildScreen(BuildContext context, TrainingProgramOverview program) {
    _logger.v('$runtimeType._buildScreen()');
    return Scaffold(
      appBar: AppBar(
        title: Text(program.name),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Add Week to Program',
        child: Icon(Icons.add),
        onPressed: () {
          // TODO go to week creation screen
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
              builder: (_, snapshot) => snapshot.waitSwitch(
                Consumer<TrainingProgramProvider>(
                  builder: (_, providerData, __) => Scrollbar(
                    isAlwaysShown: true,
                    child: ListView.builder(
                      itemCount: providerData.trainingWeeksOfProgram(program.id).length,
                      itemBuilder: (_, index) => TrainingWeekCard(providerData.trainingWeeksOfProgram(program.id)[index], program.name),
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
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return _program == null ? _buildFallbackScreen() : _buildScreen(context, _program!);
  }
}
