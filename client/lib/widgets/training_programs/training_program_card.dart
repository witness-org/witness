import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/providers/training_program_provider.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/training_programs/training_program_detail_screen.dart';
import 'package:flutter/material.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

class TrainingProgramCard extends StatelessWidget with StringLocalizer {
  const TrainingProgramCard(this._trainingProgram, {final Key? key}) : super(key: key);

  final TrainingProgramOverview _trainingProgram;

  void _openDetailsScreen(final BuildContext context, final StringLocalizations uiStrings) {
    Navigator.of(context).pushNamed(TrainingProgramDetailScreen.routeName, arguments: _trainingProgram);
  }

  void _deleteTrainingProgram(final BuildContext context, final StringLocalizations uiStrings) {
    DialogHelper.getBool(
      context,
      title: uiStrings.trainingProgramCard_deleteDialog_title,
      content: '${uiStrings.trainingProgramCard_deleteDialog_content_prefix} "${_trainingProgram.name}"?',
      falseOption: uiStrings.trainingProgramCard_deleteDialog_cancel,
      trueOption: uiStrings.trainingProgramCard_deleteDialog_delete,
      trueOptionStyle: TextStyle(color: Theme.of(context).colorScheme.error),
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
    final uiStrings = getLocalizedStrings(context);
    return TrainingProgramComponentCard(
      _trainingProgram.name,
      [
        _trainingProgram.description,
        _trainingProgram.numberOfWeeks.toNumberString(uiStrings.trainingProgramCard_weeksLabel_noun),
      ],
      _openDetailsScreen,
      _deleteTrainingProgram,
    );
  }
}
