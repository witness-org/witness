import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/days/training_day_detail_screen.dart';
import 'package:flutter/material.dart';

class TrainingDayCard extends StatelessWidget {
  const TrainingDayCard(this._day, this._weekNumber, {final Key? key}) : super(key: key);

  final TrainingDayOverview _day;
  final int _weekNumber;

  void _openDetailsScreen(final BuildContext context) {
    Navigator.of(context).pushNamed(TrainingDayDetailScreen.routeName, arguments: [_day, _weekNumber]);
  }

  Future<void> _deleteTrainingDay(final BuildContext context) async {
    // TODO(raffaelfoidl-leabrugger): delete training day (only from this training week)
    // ignore: unused_local_variable
    final deleteDay = await DialogHelper.getBool(
      context,
      title: 'Delete Training Day?',
      content: 'Are you sure you want to delete Day ${_day.number}?',
      falseOption: 'Cancel',
      trueOption: 'Delete',
      trueOptionStyle: TextStyle(color: Theme.of(context).errorColor),
    );
  }

  @override
  Widget build(final BuildContext context) {
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
