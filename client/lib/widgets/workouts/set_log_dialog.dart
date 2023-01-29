import 'package:client/extensions/context_extensions.dart';
import 'package:client/extensions/enum_extensions.dart';
import 'package:client/extensions/number_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/workouts/exercise_log.dart';
import 'package:client/models/workouts/resistance_band.dart';
import 'package:client/models/workouts/set_log.dart';
import 'package:client/models/workouts/workout_log.dart';
import 'package:client/widgets/common/requester_state.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/workouts/set_log_form_input.dart';
import 'package:flutter/material.dart';

final _logger = getLogger('set_log_dialog');

class SetLogDialog extends StatefulWidget {
  const SetLogDialog(
    this._exerciseLog,
    this._setLog,
    this._submitSetLogForm, {
    final Future<void> Function(BuildContext context, SetLog setLog)? deleteSetLog,
    final Key? key,
  })  : _deleteSetLog = deleteSetLog,
        super(key: key);

  final ExerciseLog _exerciseLog;
  final SetLog? _setLog;
  final Future<void> Function(BuildContext context, SetLogFormInput setLogForm) _submitSetLogForm;
  final Future<void> Function(BuildContext context, SetLog setLog)? _deleteSetLog;

  @override
  State<StatefulWidget> createState() => _SetLogDialogState();
}

class _SetLogDialogState extends RequesterState<SetLogDialog, WorkoutLog> with StringLocalizer, LogMessagePreparer {
  final _formKey = GlobalKey<FormState>();
  SetLogFormInput? _formInput;

  Widget _buildErrorDialog(final StringLocalizations uiStrings) {
    return AlertDialog(
      content: Text(uiStrings.exerciseLogItem_setLogDialog_error),
      actions: const [],
    );
  }

  Widget _buildPaddedFormField(final Widget child) {
    return Padding(
      padding: const EdgeInsets.only(top: 10.0),
      child: child,
    );
  }

  Widget _buildWeightFormField(final StringLocalizations uiStrings, final SetLogFormInput formInput) {
    return TextFormField(
      decoration: InputDecoration(
        hintText: uiStrings.exerciseLogItem_setLogDialog_weightHint,
        labelText: uiStrings.exerciseLogItem_setLogDialog_weightLabel,
        border: const OutlineInputBorder(),
        suffix: Text(uiStrings.exerciseLogItem_setLogDialog_weightSuffixKg), // TODO(leabrugger-raffaelfoidl): also display in lbs
      ),
      initialValue: formInput.weightG.gInKg.toString(),
      keyboardType: TextInputType.number,
      validator: (final value) {
        if (value == null) {
          return uiStrings.exerciseLogItem_setLogDialog_weightNotSetError;
        }

        if (double.tryParse(value) == null) {
          return uiStrings.exerciseLogItem_setLogDialog_weightInvalidError;
        }
        return null;
      },
      onSaved: (final value) => value != null ? formInput.weightG = double.parse(value).kgInG : 0,
    );
  }

  Widget _buildRepsTimeMenu(final StringLocalizations uiStrings, final SetLogFormInput formInput) {
    return DropdownButton<LoggingType>(
      icon: const Icon(Icons.arrow_drop_down),
      value: formInput.loggingType,
      elevation: 16,
      onChanged: (final LoggingType? newValue) {
        if (newValue != null) {
          setState(() => formInput.loggingType = newValue);
        }
      },
      items: widget._exerciseLog.exercise.loggingTypes.map<DropdownMenuItem<LoggingType>>((final LoggingType type) {
        return DropdownMenuItem<LoggingType>(
          value: type,
          child: Text(type.toUiUnitString()),
        );
      }).toList(),
    );
  }

  Widget _buildRepsTimeFormField(final StringLocalizations uiStrings, final SetLogFormInput formInput) {
    return Row(children: [
      Expanded(
        child: Padding(
          padding: const EdgeInsets.only(right: 10.0),
          child: TextFormField(
            decoration: InputDecoration(
              hintText: uiStrings.exerciseLogItem_setLogDialog_repsTimeHint,
              labelText: formInput.loggingType.toUiString(),
              border: const OutlineInputBorder(),
            ),
            initialValue: formInput.loggedValue.toString(),
            keyboardType: TextInputType.number,
            validator: (final value) {
              if (value == null) {
                return uiStrings.exerciseLogItem_setLogDialog_repsTimeNotSetError;
              }

              final intValue = int.tryParse(value);

              if (intValue == null) {
                return uiStrings.exerciseLogItem_setLogDialog_repsTimeNotSetError;
              } else if (intValue < 1) {
                return uiStrings.exerciseLogItem_setLogDialog_repsTimeInvalidError;
              }
              return null;
            },
            onSaved: (final newValue) => newValue != null ? formInput.loggedValue = int.parse(newValue) : 0,
          ),
        ),
      ),
      _buildRepsTimeMenu(uiStrings, formInput),
    ]);
  }

  Widget _buildRpeFormField(final StringLocalizations uiStrings, final SetLogFormInput formInput) {
    return Row(
      children: [
        Checkbox(
          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
          visualDensity: const VisualDensity(horizontal: -4, vertical: -2.75),
          value: formInput.rpe != null,
          onChanged: (final bool? value) {
            if (value != null) {
              setState(() => formInput.rpe = value ? 0 : null);
            }
          },
        ),
        Padding(
          padding: const EdgeInsets.only(left: 8),
          child: Text(uiStrings.exerciseLogItem_setLogDialog_rpeLabel),
        ),
        Expanded(
          child: Slider(
            value: formInput.rpe != null ? formInput.rpe!.toDouble() : 0.0,
            max: 10,
            divisions: 10,
            label: formInput.rpe?.toString(),
            onChanged: (final value) {
              setState(() => formInput.rpe = value.round());
            },
          ),
        ),
      ],
    );
  }

  Widget _buildBandsFormField(final StringLocalizations uiStrings, final SetLogFormInput formInput, final bool isDarkModeEnabled) {
    return SizedBox(
      width: double.infinity,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(uiStrings.exerciseLogItem_setLogDialog_resistanceBandsHeading),
          Wrap(
            spacing: 2,
            children: ResistanceBand.values.map((final band) {
              final chipColor = band.mapToColor().withOpacity(0.4);
              return ChoiceChip(
                  avatar: formInput.resistanceBands[band]! ? const Icon(Icons.check) : null,
                  labelStyle: TextStyle(color: !isDarkModeEnabled ? Colors.black : Colors.white),
                  backgroundColor: chipColor,
                  selectedColor: chipColor,
                  selected: formInput.resistanceBands[band]!,
                  label: Text(band.toUiString()),
                  onSelected: (final selected) {
                    setState(() {
                      formInput.resistanceBands[band] = selected;
                      _formInput = formInput;
                    });
                  });
            }).toList(),
          ),
        ],
      ),
    );
  }

  Widget _buildScrollableForm(final StringLocalizations uiStrings, final SetLogFormInput formInput, final bool isDarkModeEnabled) {
    return SingleChildScrollView(
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            _buildPaddedFormField(_buildWeightFormField(uiStrings, formInput)),
            _buildPaddedFormField(_buildRepsTimeFormField(uiStrings, formInput)),
            _buildPaddedFormField(_buildRpeFormField(uiStrings, formInput)),
            _buildPaddedFormField(_buildBandsFormField(uiStrings, formInput, isDarkModeEnabled)),
          ],
        ),
      ),
    );
  }

  List<Widget> _buildActionButtons(final BuildContext context, final StringLocalizations uiStrings, final SetLogFormInput formInput) {
    return [
      TextButton(
        onPressed: () => Navigator.pop(context),
        child: Text(uiStrings.exerciseLogItem_setLogDialog_cancel),
      ),
      if (widget._setLog != null && widget._deleteSetLog != null)
        TextButton(
          style: TextButton.styleFrom(foregroundColor: Theme.of(context).colorScheme.error),
          onPressed: () {
            Navigator.pop(context);
            widget._deleteSetLog!(context, widget._setLog!);
          },
          child: Text(uiStrings.exerciseLogItem_setLogDialog_delete),
        ),
      TextButton(
        onPressed: () {
          if (_formKey.currentState != null && _formKey.currentState!.validate()) {
            _formKey.currentState!.save();
            Navigator.pop(context);
            widget._submitSetLogForm(context, formInput);
          }
        },
        child: Text(uiStrings.exerciseLogItem_setLogDialog_confirm),
      ),
    ];
  }

  @override
  void initState() {
    super.initState();

    if (widget._exerciseLog.exercise.loggingTypes.isNotEmpty) {
      _formInput = widget._setLog != null
          ? SetLogFormInput.editForm(widget._setLog!)
          : SetLogFormInput.createForm(widget._exerciseLog.exercise.loggingTypes[0]);
    }
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));
    final uiStrings = getLocalizedStrings(context);
    final isDarkModeEnabled = context.isDarkModeEnabled();
    return _formInput != null
        ? AlertDialog(
            scrollable: true,
            actionsPadding: EdgeInsets.zero,
            contentPadding: const EdgeInsets.symmetric(horizontal: 20.0),
            title: Text(widget._exerciseLog.exercise.name),
            content: _buildScrollableForm(uiStrings, _formInput!, isDarkModeEnabled),
            actions: _buildActionButtons(context, uiStrings, _formInput!),
          )
        : _buildErrorDialog(uiStrings);
  }
}
