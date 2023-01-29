import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/days/training_day_detail_screen.dart';
import 'package:flutter/material.dart';

class TrainingDayCard extends StatelessWidget with StringLocalizer {
  const TrainingDayCard(this._day, this._weekNumber, {final Key? key}) : super(key: key);

  final TrainingDayOverview _day;
  final int _weekNumber;

  void _openDetailsScreen(final BuildContext context, final StringLocalizations uiStrings) {
    Navigator.of(context).pushNamed(TrainingDayDetailScreen.routeName, arguments: [_day, _weekNumber]);
  }

  Future<void> _deleteTrainingDay(final BuildContext context, final StringLocalizations uiStrings) async {
    // TODO(raffaelfoidl-leabrugger): delete training day (only from this training week)
    // ignore: unused_local_variable
    final deleteDay = await DialogHelper.getBool(
      context,
      title: uiStrings.trainingDayCard_deleteDialog_title,
      content: '${uiStrings.trainingDayCard_deleteDialog_content_prefix} ${_day.number}?',
      falseOption: uiStrings.trainingDayCard_deleteDialog_cancel,
      trueOption: uiStrings.trainingDayCard_deleteDialog_delete,
      trueOptionStyle: TextStyle(color: Theme.of(context).colorScheme.error),
    );
  }

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return TrainingProgramComponentCard(
      '${uiStrings.trainingDayCard_dayNumber_prefix} ${_day.number}: ${_day.name}',
      [
        _day.description,
        _day.numberOfWorkouts.toNumberString(uiStrings.trainingDayCard_workoutsLabel_noun),
      ],
      _openDetailsScreen,
      _deleteTrainingDay,
    );
  }
}
