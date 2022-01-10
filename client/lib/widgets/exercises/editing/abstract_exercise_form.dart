import 'package:client/extensions/enum_extensions.dart';
import 'package:client/extensions/string_extensions.dart';
import 'package:client/logging/log_message_preparer.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/services/server_response.dart';
import 'package:client/widgets/common/requester_state.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/editing/exercise_form_input.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

final _logger = getLogger('abstract_exercise_form');

abstract class AbstractExerciseForm extends StatefulWidget {
  const AbstractExerciseForm({final Key? key, final this.exercise}) : super(key: key);

  final Exercise? exercise;
}

abstract class AbstractExerciseFormState<T extends AbstractExerciseForm> extends RequesterState<T, Exercise>
    with StringLocalizer, LogMessagePreparer {
  final _formKey = GlobalKey<FormState>();
  var _formInput = ExerciseFormInput.createForm();

  Future<ServerResponse<Exercise, String>> sendRequest(final ExerciseProvider provider, final ExerciseFormInput formInput);

  void navigateAfterFormSubmit(final BuildContext context, final Exercise exercise);

  Widget _buildSaveButton(final BuildContext context, final StringLocalizations uiStrings) {
    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, bottom: 7),
      child: SizedBox(
        width: double.infinity,
        child: ElevatedButton.icon(
          onPressed: () => _submitForm(context, uiStrings),
          icon: const Icon(Icons.check),
          label: Text(uiStrings.createExerciseForm_body_save_buttonText),
        ),
      ),
    );
  }

  Widget _buildPaddedFormField(final Widget child) {
    return Padding(
      padding: const EdgeInsets.only(top: 32.0),
      child: child,
    );
  }

  Widget _buildHeading(final String text) {
    return Text(
      text,
      style: const TextStyle(
        fontSize: 16,
        fontWeight: FontWeight.bold,
      ),
    );
  }

  Widget _buildChipList(final List<Widget> children) {
    return Wrap(
      spacing: 6,
      runSpacing: 6,
      children: children,
    );
  }

  Widget _buildNameFormField(final String heading, final String placeholder, final String blankErrorText) {
    // TODO(leabrugger): disable save button if name blank (see ExerciseLogCommentDialog)
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildHeading(heading),
        TextFormField(
            initialValue: _formInput.name,
            decoration: InputDecoration(hintText: placeholder),
            validator: (final value) {
              if (value.isNullOrBlank) {
                return blankErrorText;
              }

              return null;
            },
            onSaved: (final value) => value != null ? _formInput.name = value : _formInput.name = ''),
      ],
    );
  }

  Widget _buildDescriptionFormField(final String heading, final String placeholder, final String tooLongErrorText) {
    return _buildPaddedFormField(
      Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeading(heading),
          TextFormField(
            maxLength: 1024,
            initialValue: _formInput.description ?? '',
            decoration: InputDecoration(hintText: placeholder),
            maxLines: null,
            validator: (final value) {
              if (value != null && value.length > 1024) {
                return tooLongErrorText;
              }

              return null;
            },
            onSaved: (final value) => _formInput.description = value,
          ),
        ],
      ),
    );
  }

  Widget _buildMuscleGroupFormField(final String heading) {
    return _buildPaddedFormField(
      Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeading(heading),
          _buildChipList(
            MuscleGroup.values.map<Widget>(
              (final group) {
                return ChoiceChip(
                  label: Text(group.toUiString()),
                  selected: _formInput.muscleGroups[group]!,
                  onSelected: (final selected) {
                    setState(() {
                      _formInput.muscleGroups[group] = selected;
                    });
                  },
                );
              },
            ).toList(),
          ),
        ],
      ),
    );
  }

  Widget _buildLoggingTypeFormField(final String heading) {
    return _buildPaddedFormField(
      Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeading(heading),
          _buildChipList(
            LoggingType.values.map<Widget>(
              (final type) {
                return ChoiceChip(
                    label: Text(type.toUiString()),
                    selected: _formInput.loggingTypes[type]!,
                    onSelected: (final selected) {
                      setState(() {
                        _formInput.loggingTypes[type] = selected;
                      });
                    });
              },
            ).toList(),
          ),
        ],
      ),
    );
  }

  Future<void> _submitForm(final BuildContext context, final StringLocalizations uiStrings) async {
    final provider = Provider.of<ExerciseProvider>(context, listen: false);

    final error = _validateMuscleGroupsAndLoggingTypes(uiStrings);
    if (error != null) {
      showError(error);
      return;
    }

    if (_formKey.currentState == null || !_formKey.currentState!.validate()) {
      return;
    }

    _formKey.currentState!.save();

    submitRequestWithResponse(
      () => sendRequest(provider, _formInput),
      successAction: (final success) => navigateAfterFormSubmit(context, success),
      defaultErrorMessage: uiStrings.createExerciseForm_body_errorMessage_requestFailedDefault,
    );
  }

  String? _validateMuscleGroupsAndLoggingTypes(final StringLocalizations uiStrings) {
    final noMuscleGroupSelected = !_formInput.muscleGroups.containsValue(true);
    final noLoggingTypeSelected = !_formInput.loggingTypes.containsValue(true);

    if (noMuscleGroupSelected && noLoggingTypeSelected) {
      return uiStrings.createExerciseForm_body_errorMessage_muscleGroups_loggingTypes;
    }

    if (noMuscleGroupSelected) {
      return uiStrings.createExerciseForm_body_errorMessage_muscleGroups;
    }

    if (noLoggingTypeSelected) {
      return uiStrings.createExerciseForm_body_errorMessage_loggingTypes;
    }

    return null;
  }

  @override
  void initState() {
    super.initState();

    if (widget.exercise != null) {
      _formInput = ExerciseFormInput.editForm(widget.exercise!);
    }
  }

  @override
  Widget build(final BuildContext context) {
    _logger.v(prepare('build()'));

    final uiStrings = getLocalizedStrings(context);
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Expanded(
          child: SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.only(left: 15, right: 15, top: 15),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _buildNameFormField(
                      uiStrings.createExerciseForm_body_heading_name,
                      uiStrings.createExerciseForm_body_nameInput_placeholder,
                      uiStrings.createExerciseForm_body_nameInput_blankErrorMessage,
                    ),
                    _buildDescriptionFormField(
                        uiStrings.createExerciseForm_body_heading_description,
                        uiStrings.createExerciseForm_body_descriptionInput_placeholder,
                        uiStrings.createExerciseForm_body_descriptionInput_tooLongErrorMessage),
                    _buildMuscleGroupFormField(
                      uiStrings.createExerciseForm_body_heading_muscleGroups,
                    ),
                    _buildLoggingTypeFormField(
                      uiStrings.createExerciseForm_body_heading_loggingTypes,
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
        Column(
          children: [
            const Divider(),
            _buildSaveButton(context, uiStrings),
          ],
        ),
      ],
    );
  }
}
