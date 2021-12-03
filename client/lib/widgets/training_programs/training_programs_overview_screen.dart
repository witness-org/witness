import 'package:client/extensions/async_snapshot_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/app_drawer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/main_app_bar.dart';
import 'package:client/widgets/training_programs/training_program_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('training_programs_overview_screen');

class TrainingProgramsOverviewScreen extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const TrainingProgramsOverviewScreen({final Key? key}) : super(key: key);

  static const routeName = '/training-programs-overview';

  Future<void> _fetchTrainingPrograms(final BuildContext context) async {
    _logger.v(prepare('_fetchTrainingPrograms'));
    await Provider.of<TrainingProgramProvider>(context, listen: false).fetchTrainingPrograms();
  }

  Widget _buildTrainingProgramView(final BuildContext context) {
    _logger.v(prepare('_buildTrainingProgramView()'));
    return Expanded(
      child: FutureBuilder<void>(
        future: _fetchTrainingPrograms(context),
        builder: (final _, final snapshot) => snapshot.waitSwitch(
          RefreshIndicator(
            onRefresh: () => _fetchTrainingPrograms(context),
            child: Consumer<TrainingProgramProvider>(
              builder: (final _, final providerData, final __) {
                _logger.v(prepare('_buildTrainingProgramView.Consumer.builder()'));
                return Scrollbar(
                  isAlwaysShown: true,
                  child: ListView.builder(
                    itemCount: providerData.trainingPrograms.length,
                    itemBuilder: (final _, final index) => TrainingProgramCard(providerData.trainingPrograms[index]),
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
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Scaffold(
      appBar: const MainAppBar(),
      drawer: const AppDrawer(),
      floatingActionButton: FloatingActionButton(
        tooltip: uiStrings.trainingProgramsOverviewScreen_action_newProgram,
        child: const Icon(Icons.add),
        onPressed: () {
          // TODO(raffaelfoidl-leabrugger): Go to training program creation screen
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
