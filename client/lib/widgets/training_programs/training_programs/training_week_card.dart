import 'package:client/extensions/number_extensions.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/widgets/common/dialog_helper.dart';
import 'package:client/widgets/training_programs/common/training_program_component_card.dart';
import 'package:client/widgets/training_programs/weeks/training_week_detail_screen.dart';
import 'package:flutter/material.dart';

class TrainingWeekCard extends StatelessWidget {
  final TrainingWeekOverview _week;
  final String _programName;

  const TrainingWeekCard(this._week, this._programName, {Key? key}) : super(key: key);

  void _openDetailsScreen(BuildContext context) {
    Navigator.of(context).pushNamed(TrainingWeekDetailScreen.routeName, arguments: [_week, _programName]);
  }

  void _deleteTrainingWeek(BuildContext context) async {
    // TODO delete training week (only from this training program)
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
  Widget build(BuildContext context) {
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
