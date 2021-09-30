import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/common/training_program_component_header.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('training_week_header');

class TrainingWeekHeader extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const TrainingWeekHeader(this._week, this._programName, {final Key? key}) : super(key: key);

  final TrainingWeekOverview _week;
  final String _programName;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return TrainingProgramComponentHeader(
      [
        Text(
          '$_programName (${uiStrings.trainingWeekHeader_weekNumber_prefix} ${_week.number})',
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (_week.description != null) Text(_week.description!),
        const SizedBox(height: 3),
      ],
    );
  }
}
