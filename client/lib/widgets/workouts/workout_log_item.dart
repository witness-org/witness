import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/workout_log_form_input.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/providers/workout_log_provider.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/exercise_log_item.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('workout_log_item');

class WorkoutLogItem extends StatefulWidget with LogMessagePreparer {
  WorkoutLogItem(this.index, this.workoutLog, {final Key? key}) : super(key: key);
  final int index;
  final WorkoutLog workoutLog;

  @override
  State<StatefulWidget> createState() => WorkoutLogItemState();
}

class WorkoutLogItemState extends State<WorkoutLogItem> with StringLocalizer, LogMessagePreparer {
  final _formKey = GlobalKey<FormState>();
  WorkoutLog? _workoutLog;
  WorkoutLogFormInput? _formInput;
  int? _index;

  @override
  void initState() {
    super.initState();

    _workoutLog = widget.workoutLog;
    _formInput = WorkoutLogFormInput.editForm(_workoutLog!);
    _index = widget.index;
  }

  Widget _buildWorkoutHeader(final String heading) {
    return Text(
      heading,
      style: const TextStyle(
        fontWeight: FontWeight.bold,
        fontSize: 16,
      ),
    );
  }

  Widget _buildWorkoutDurationButton(final String buttonText, final Function() onPressed) {
    return TextButton.icon(
      icon: const Icon(Icons.timer),
      label: Text(
        buttonText,
        style: const TextStyle(
          fontSize: 16,
        ),
      ),
      onPressed: onPressed,
    );
  }

  Widget _buildExerciseLogList(final List<ExerciseLog> exerciseLogs) {
    return Column(
      children: <Widget>[
        ...exerciseLogs.map((final item) {
          return ExerciseLogItem(item);
        }).toList()
      ],
    );
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            _buildWorkoutHeader(
              uiStrings.workoutLogScreen_workoutLog_heading(_index! + 1),
            ),
            _buildWorkoutDurationButton(
              uiStrings.workoutLogScreen_workoutLog_duration(_formInput!.durationMinutes ?? 0),
              () => showDialog<String>(
                context: context,
                builder: (final BuildContext context) => _WorkoutDurationDialog(_formKey, _formInput!, _submitForm),
              ),
            ),
          ],
        ),
        _buildExerciseLogList(_workoutLog!.exerciseLogs),
      ],
    );
  }

  Future<void> _submitForm(final BuildContext context, final StringLocalizations uiStrings, final WorkoutLogFormInput formInput) async {
    final provider = Provider.of<WorkoutLogProvider>(context, listen: false);

    if (_formKey.currentState == null || !_formKey.currentState!.validate()) {
      return;
    }

    _formKey.currentState!.save();

    await ProgressLoader().show(context);
    final response = await provider.patchWorkoutLogDuration(formInput);
    await ProgressLoader().dismiss();

    if (response.isSuccessAndResponse) {
      if (!mounted) {
        return;
      }

      setState(() {});
      Navigator.pop(context);
    } else {
      _showError(response.error != null ? response.error! : uiStrings.workoutLogScreen_workoutDurationDialog_durationDefaultError);
    }
  }

  void _showError(final String text) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(text)),
    );
  }
}

class _WorkoutDurationDialog extends StatelessWidget with StringLocalizer, LogMessagePreparer {
  const _WorkoutDurationDialog(this._formKey, this._formInput, this._submitFormFunction);

  final GlobalKey<FormState> _formKey;
  final WorkoutLogFormInput _formInput;
  final Future<void> Function(BuildContext, StringLocalizations, WorkoutLogFormInput) _submitFormFunction;

  String? _validateDuration(final String? value, final StringLocalizations uiStrings) {
    try {
      if (value != null && int.parse(value) < 0) {
        return uiStrings.workoutLogScreen_workoutDurationDialog_durationInvalidError;
      }
    } on FormatException {
      return uiStrings.workoutLogScreen_workoutDurationDialog_durationInvalidError;
    }

    return null;
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    return AlertDialog(
      title: Text(uiStrings.workoutLogScreen_workoutDurationDialog_title),
      content: Form(
        key: _formKey,
        child: TextFormField(
          decoration: InputDecoration(
            icon: const Icon(Icons.timer),
            hintText: uiStrings.workoutLogScreen_workoutDurationDialog_durationHint,
            labelText: uiStrings.workoutLogScreen_workoutDurationDialog_durationLabel,
            border: const OutlineInputBorder(),
            suffix: Text(uiStrings.workoutLogScreen_workoutDurationDialog_durationSuffix),
          ),
          keyboardType: TextInputType.number,
          initialValue: _formInput.durationMinutes.toString(),
          validator: (final value) => _validateDuration(value, uiStrings),
          onSaved: (final value) => _formInput.durationMinutes = value != null ? int.parse(value) : null,
        ),
      ),
      actions: <Widget>[
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(uiStrings.workoutLogScreen_workoutDurationDialog_cancel),
        ),
        TextButton(
          onPressed: () => {_submitFormFunction(context, uiStrings, _formInput)},
          child: Text(uiStrings.workoutLogScreen_workoutDurationDialog_confirm),
        ),
      ],
    );
  }
}
