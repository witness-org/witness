import 'package:client/extensions/number_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/widgets/training_programs/common/labelled_checkbox.dart';
import 'package:client/widgets/training_programs/common/training_program_component_header.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('training_program_header');

class TrainingProgramHeader extends StatefulWidget {
  const TrainingProgramHeader(this._program, {final Key? key}) : super(key: key);

  final TrainingProgramOverview _program;

  @override
  State<TrainingProgramHeader> createState() => _TrainingProgramHeaderState();
}

class _TrainingProgramHeaderState extends State<TrainingProgramHeader> with LogMessagePreparer {
  // The initializer of late variable that are initialized at declaration runs the first time the variable is used (lazy initialization).
  // This way, we do not need to override initState().
  late bool _isPublished = widget._program.isPublished;

  void _publishCheckedChanged(final bool newValue) {
    // todo provider call, provider calls service, widget._program is reloaded/updated...
    setState(() => _isPublished = newValue);
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    return TrainingProgramComponentHeader(
      [
        Text(
          widget._program.name,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        if (widget._program.description != null) Text(widget._program.description!),
        const SizedBox(height: 5),
        Text(widget._program.numberOfWeeks.toNumberString('week')),
        LabelledCheckbox(
          label: 'Program publicly available',
          value: _isPublished,
          onCheckedChanged: _publishCheckedChanged,
        ),
      ],
    );
  }
}
