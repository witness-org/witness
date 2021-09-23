import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/weeks/training_week_detail_screen.dart';
import 'package:flutter/material.dart';

class TrainingWeekCard extends StatelessWidget {
  const TrainingWeekCard(this._week, this._programName, {final Key? key}) : super(key: key);

  final TrainingWeekOverview _week;
  final String _programName;

  void _openDetailsScreen(final BuildContext context) {
    Navigator.of(context).pushNamed(TrainingWeekDetailScreen.routeName, arguments: [_week, _programName]);
  }

  Future<void> _deleteTrainingWeek(final BuildContext context) async {
    // TODO(raffaelfoidl-leabrugger): delete training week (only from this training program)
    // ignore: unused_local_variable
    final deleteWeek = await DialogHelper.getBool(
      context,
      title: 'Delete Training Week?',
      content: 'Are you sure you want to delete Week ${_week.id}?',
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
      'Week ${_week.number}',
      [
        _week.description,
        '${_week.numberOfDays.toNumberString('day')} (${_week.totalNumberOfWorkouts.toNumberString('workout')} overall)',
      ],
      _openDetailsScreen,
      _deleteTrainingWeek,
    );
  }
}
