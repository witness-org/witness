import 'package:client/extensions/enum_extensions.dart';
import 'package:client/models/exercises/exercise_create.dart';
import 'package:client/models/exercises/logging_type.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:progress_loader_overlay/progress_loader_overlay.dart';
import 'package:provider/provider.dart';

class CreateExerciseForm extends StatefulWidget {
  const CreateExerciseForm({final Key? key}) : super(key: key);

  @override
  _CreateExerciseFormState createState() => _CreateExerciseFormState(); // ignore: library_private_types_in_public_api
}

class _CreateExerciseFormState extends State<CreateExerciseForm> with StringLocalizer {
  final _formKey = GlobalKey<FormState>();
  final _ExerciseFormInput _formInput = _ExerciseFormInput();

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

  Widget _buildNameFormField(final String heading, final String placeholder, final String emptyErrorText) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildHeading(heading),
        TextFormField(
            initialValue: '',
            decoration: InputDecoration(hintText: placeholder),
            validator: (final value) {
              if (value == null || value.isEmpty) {
                return emptyErrorText;
              }

              return null;
            },
            onSaved: (final value) => value != null ? _formInput.name = value : _formInput.name = ''),
      ],
    );
  }

  Widget _buildDescriptionFormField(final String heading, final String placeholder) {
    return _buildPaddedFormField(
      Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeading(heading),
          TextFormField(
            decoration: InputDecoration(hintText: placeholder),
            maxLines: null,
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

  @override
  Widget build(final BuildContext context) {
    final uiStrings = getLocalizedStrings(context);
    return ListView(
      padding: const EdgeInsets.all(20.0),
      children: [
        Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildNameFormField(
                uiStrings.createExerciseForm_body_heading_name,
                uiStrings.createExerciseForm_body_nameInput_placeholder,
                uiStrings.createExerciseForm_body_nameInput_emptyErrorMessage,
              ),
              _buildDescriptionFormField(
                uiStrings.createExerciseForm_body_heading_description,
                uiStrings.createExerciseForm_body_descriptionInput_placeholder,
              ),
              _buildMuscleGroupFormField(
                uiStrings.createExerciseForm_body_heading_muscleGroups,
              ),
              _buildLoggingTypeFormField(
                uiStrings.createExerciseForm_body_heading_loggingTypes,
              ),
              Center(
                child: SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: () => _submitForm(context, uiStrings),
                    child: Text(uiStrings.createExerciseForm_body_save_buttonText),
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Future<void> _submitForm(final BuildContext context, final StringLocalizations uiStrings) async {
    final provider = Provider.of<ExerciseProvider>(context, listen: false);

    final error = _validateMuscleGroupsAndLoggingTypes(uiStrings);
    if (error != null) {
      _showError(error);
      return;
    }

    if (_formKey.currentState == null || !_formKey.currentState!.validate()) {
      return;
    }

    _formKey.currentState!.save();

    await ProgressLoader().show(context);
    final exerciseCreate = _getExerciseCreate();
    final response = await provider.postUserExercise(exerciseCreate);
    await ProgressLoader().dismiss();

    if (response.isSuccessAndResponse) {
      if (!mounted) {
        return;
      }

      Navigator.of(context).pushReplacementNamed(ExerciseDetailScreen.routeName, arguments: response.success);
    } else {
      _showError(response.error != null ? response.error! : uiStrings.createExerciseForm_body_errorMessage_requestFailedDefault);
    }
  }

  ExerciseCreate _getExerciseCreate() {
    final List<MuscleGroup> muscleGroups = [];
    final List<LoggingType> loggingTypes = [];
    for (final element in _formInput.muscleGroups.entries) {
      if (element.value) {
        muscleGroups.add(element.key);
      }
    }

    for (final element in _formInput.loggingTypes.entries) {
      if (element.value) {
        loggingTypes.add(element.key);
      }
    }

    final description = _formInput.description != null && _formInput.description!.isEmpty ? null : _formInput.description;

    return ExerciseCreate(name: _formInput.name, description: description, muscleGroups: muscleGroups, loggingTypes: loggingTypes);
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

  void _showError(final String text) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(text)),
    );
  }
}

class _ExerciseFormInput {
  _ExerciseFormInput({final this.name = ''});

  String name;
  String? description;
  Map<MuscleGroup, bool> muscleGroups = {for (final group in MuscleGroup.values) group: false};
  Map<LoggingType, bool> loggingTypes = {for (var type in LoggingType.values) type: false};
}
