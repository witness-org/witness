import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/widgets/training_programs/common/segmented_text.dart';
import 'package:client/widgets/training_programs/common/training_program_component_header.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('training_day_header');

class TrainingDayHeader extends StatelessWidget {
  final TrainingDayOverview _day;

  const TrainingDayHeader(this._day, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    _logger.v('$runtimeType.build()');
    return TrainingProgramComponentHeader(
      [
        SegmentedText(
          baseStyle: Theme.of(context).textTheme.bodyText2?.merge(const TextStyle(fontSize: 16)),
          segments: [
            TextSegment(
              'Day ${_day.number}',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            TextSegment(_day.name, prefix: ' (', suffix: ')'),
          ],
        ),
        if (_day.description != null) Text(_day.description!),
        SizedBox(height: 10),
      ],
    );
  }
}
