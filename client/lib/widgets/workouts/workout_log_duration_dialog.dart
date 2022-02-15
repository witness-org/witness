import 'package:client/logging/log_message_preparer.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('workout_log_duration_dialog');

class WorkoutLogDurationDialog extends StatefulWidget {
  const WorkoutLogDurationDialog(this._durationMinutes, this._updateWorkoutDuration, {final Key? key}) : super(key: key);

  final int _durationMinutes;
  final Future<void> Function(BuildContext context, int? updatedDuration) _updateWorkoutDuration;

  @override
  State<StatefulWidget> createState() => _WorkoutLogDurationDialogState();
}

class _WorkoutLogDurationDialogState extends State<WorkoutLogDurationDialog> with StringLocalizer, LogMessagePreparer {
  final _formKey = GlobalKey<FormState>();
  late int _durationMinutes = widget._durationMinutes;

  String? _validateDuration(final String? value, final StringLocalizations uiStrings) {
    final intValue = value != null ? int.tryParse(value) : null;
    return intValue == null || intValue < 0 ? uiStrings.workoutLogItem_workoutDurationDialog_durationInvalidError : null;
  }

  Widget _buildForm(final StringLocalizations uiStrings) {
    return Form(
      key: _formKey,
      child: TextFormField(
        decoration: InputDecoration(
          icon: const Icon(Icons.timer),
          hintText: uiStrings.workoutLogItem_workoutDurationDialog_durationHint,
          labelText: uiStrings.workoutLogItem_workoutDurationDialog_durationLabel,
          border: const OutlineInputBorder(),
          suffix: Text(uiStrings.workoutLogItem_workoutDurationDialog_durationSuffix),
        ),
        initialValue: _durationMinutes.toString(),
        keyboardType: TextInputType.number,
        validator: (final value) => _validateDuration(value, uiStrings),
        onSaved: (final value) => _durationMinutes = value != null ? int.parse(value) : 0,
      ),
    );
  }

  List<Widget> _buildActionButtons(final BuildContext context, final StringLocalizations uiStrings) {
    return [
      TextButton(
        onPressed: () => Navigator.pop(context),
        child: Text(uiStrings.workoutLogItem_workoutDurationDialog_cancel),
      ),
      TextButton(
        onPressed: () {
          if (_formKey.currentState != null && _formKey.currentState!.validate()) {
            _formKey.currentState!.save();
            Navigator.pop(context);
            widget._updateWorkoutDuration(context, _durationMinutes);
          }
        },
        child: Text(uiStrings.workoutLogItem_workoutDurationDialog_confirm),
      ),
    ];
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return AlertDialog(
      title: Text(uiStrings.workoutLogItem_workoutDurationDialog_title),
      content: _buildForm(uiStrings),
      actions: _buildActionButtons(context, uiStrings),
    );
  }
}
