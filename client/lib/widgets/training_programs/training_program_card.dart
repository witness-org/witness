import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/training_programs/training_program_detail_screen.dart';
import 'package:flutter/material.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

class TrainingProgramCard extends StatelessWidget {
  const TrainingProgramCard(final this._trainingProgram, {final Key? key}) : super(key: key);

  final TrainingProgramOverview _trainingProgram;

  void _openDetailsScreen(final BuildContext context) {
    Navigator.of(context).pushNamed(TrainingProgramDetailScreen.routeName, arguments: _trainingProgram);
  }

  void _deleteTrainingProgram(final BuildContext context) {
    DialogHelper.getBool(
      context,
      title: 'Delete Training Program?',
      content: 'Are you sure you want to delete "${_trainingProgram.name}"?',
      falseOption: 'Cancel',
      trueOption: 'Delete',
      trueOptionStyle: TextStyle(color: Theme.of(context).errorColor),
    ).then(
      (final deleteProgram) {
        if (deleteProgram != true) {
          return;
        }
        ProgressLoader()
            .show(context)
            .then((final _) => Provider.of<TrainingProgramProvider>(context, listen: false).deleteTrainingProgram(_trainingProgram.id))
            .then((final _) => ProgressLoader().dismiss());
      },
    );
  }

  @override
  Widget build(final BuildContext context) {
    // Although this widget only returns one single other widget, I leave it as is since the appearance of this widget
    // is subject to change and - from a factorization point of view - it should have its own dedicated library.
    return TrainingProgramComponentCard(
      _trainingProgram.name,
      [
        _trainingProgram.description,
        _trainingProgram.numberOfWeeks.toNumberString('week'),
      ],
      _openDetailsScreen,
      _deleteTrainingProgram,
    );
  }
}
