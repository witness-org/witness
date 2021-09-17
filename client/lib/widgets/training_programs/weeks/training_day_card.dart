import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/days/training_day_detail_screen.dart';
import 'package:flutter/material.dart';

class TrainingDayCard extends StatelessWidget {
  final TrainingDayOverview _day;
  final int _weekNumber;

  const TrainingDayCard(this._day, this._weekNumber, {Key? key}) : super(key: key);

  void _openDetailsScreen(BuildContext context) {
    Navigator.of(context).pushNamed(TrainingDayDetailScreen.routeName, arguments: [_day, _weekNumber]);
  }

  void _deleteTrainingDay(BuildContext context) async {
    // TODO delete training day (only from this training week)
    final deleteWeek = await DialogHelper.getBool(
      context,
      title: 'Delete Training Day?',
      content: 'Are you sure you want to delete Day ${_day.number}?',
      falseOption: 'Cancel',
      trueOption: 'Delete',
      trueOptionStyle: TextStyle(color: Theme.of(context).errorColor),
    );
  }

  @override
  Widget build(BuildContext context) {
    // Although this widget only returns one single other widget, I leave it as is since the appearance of this widget
    // is subject to change and - from a factorization point of view - it should have its own dedicated library.
    return TrainingProgramComponentCard(
      'Day ${_day.number}: ${_day.name}',
      [
        _day.description,
        _day.numberOfWorkouts.toNumberString('workout'),
      ],
      _openDetailsScreen,
      _deleteTrainingDay,
    );
  }
}
