import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/widgets/training_programs/common/training_program_component_header.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('training_week_header');

class TrainingWeekHeader extends StatelessWidget {
  final TrainingWeekOverview _week;
  final String _programName;

  const TrainingWeekHeader(this._week, this._programName, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return TrainingProgramComponentHeader(
      [
        Text(
          '$_programName (Week ${_week.number})',
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (_week.description != null) Text(_week.description!),
        SizedBox(height: 3),
      ],
    );
  }
}
