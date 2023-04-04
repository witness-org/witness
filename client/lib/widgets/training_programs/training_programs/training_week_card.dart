import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/weeks/training_week_detail_screen.dart';
import 'package:flutter/material.dart';

class TrainingWeekCard extends StatelessWidget with StringLocalizer {
  const TrainingWeekCard(this._week, this._programName, {final Key? key}) : super(key: key);

  final TrainingWeekOverview _week;
  final String _programName;

  void _openDetailsScreen(final BuildContext context, final StringLocalizations uiStrings) {
    Navigator.of(context).pushNamed(TrainingWeekDetailScreen.routeName, arguments: [_week, _programName]);
  }

  Future<void> _deleteTrainingWeek(final BuildContext context, final StringLocalizations uiStrings) async {
    // TODO(raffaelfoidl-leabrugger): delete training week (only from this training program)
    // ignore: unused_local_variable
    final deleteWeek = await DialogHelper.getBool(
      context,
      title: uiStrings.trainingWeekCard_deleteDialog_title,
      content: '${uiStrings.trainingWeekCard_deleteDialog_content_prefix} ${_week.number}?',
      falseOption: uiStrings.trainingWeekCard_deleteDialog_cancel,
      trueOption: uiStrings.trainingWeekCard_deleteDialog_delete,
      trueOptionStyle: TextStyle(color: Theme.of(context).colorScheme.error),
    );
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return TrainingProgramComponentCard(
      '${uiStrings.trainingWeekCard_weekNumber_prefix} ${_week.number}',
      [
        _week.description,
        '${_week.numberOfDays.toNumberString(uiStrings.trainingWeekCard_daysLabel_noun)} '
            '(${_week.totalNumberOfWorkouts.toNumberString(uiStrings.trainingWeekCard_workoutsLabel_noun)} '
            '${uiStrings.trainingWeekCard_workoutsLabel_suffix})',
      ],
      _openDetailsScreen,
      _deleteTrainingWeek,
    );
  }
}
