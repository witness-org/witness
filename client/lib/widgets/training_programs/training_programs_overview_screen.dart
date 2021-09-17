import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:client/widgets/training_programs/training_program_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('training_programs_overview_screen');

class TrainingProgramsOverviewScreen extends StatelessWidget {
  static const routeName = '/training-programs-overview';

  const TrainingProgramsOverviewScreen({Key? key}) : super(key: key);

  Future<void> _fetchTrainingPrograms(BuildContext context) async {
    _logger.v('$runtimeType._fetchTrainingPrograms');
    await Provider.of<TrainingProgramProvider>(context, listen: false).fetchTrainingPrograms();
  }

  Widget _buildTrainingProgramView(BuildContext context) {
    _logger.v('$runtimeType._buildTrainingProgramView()');
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchTrainingPrograms(context),
        builder: (_, snapshot) => snapshot.waitSwitch(
          RefreshIndicator(
            onRefresh: () => _fetchTrainingPrograms(context),
            child: Consumer<TrainingProgramProvider>(
              builder: (_, providerData, __) {
                _logger.v('$runtimeType._buildTrainingProgramView.Consumer.builder()');
                return Scrollbar(
                  isAlwaysShown: true,
                  child: ListView.builder(
                    itemCount: providerData.trainingPrograms.length,
                    itemBuilder: (_, index) => TrainingProgramCard(providerData.trainingPrograms[index]),
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return Scaffold(
      appBar: MainAppBar(),
      drawer: AppDrawer(),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Create new Training Program',
        child: Icon(Icons.add),
        onPressed: () {
          // TODO go to training program creation screen
        },
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildTrainingProgramView(context),
        ],
      ),
    );
  }
}
