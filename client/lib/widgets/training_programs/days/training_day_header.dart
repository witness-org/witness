import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:client/widgets/training_programs/common/training_program_component_header.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('training_day_header');

class TrainingDayHeader extends StatelessWidget with LogMessagePreparer, StringLocalizer {
  const TrainingDayHeader(this._day, {final Key? key}) : super(key: key);

  final TrainingDayOverview _day;

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return TrainingProgramComponentHeader(
      [
        SegmentedText(
          baseStyle: Theme.of(context).textTheme.bodyText2?.merge(const TextStyle(fontSize: 16)),
          segments: [
            TextSegment(
              '${uiStrings.trainingDayHeader_dayNumber_prefix} ${_day.number}',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
            TextSegment(_day.name, prefix: ' (', suffix: ')'),
          ],
        ),
        if (_day.description != null) Text(_day.description!),
        const SizedBox(height: 10),
      ],
    );
  }
}
