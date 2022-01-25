import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_create.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/services/server_response.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:flutter/material.dart';

import 'package:client/widgets/exercises/editing/abstract_exercise_form.dart';
import 'package:client/widgets/exercises/editing/exercise_form_input.dart';

class CreateExerciseForm extends AbstractExerciseForm {
  const CreateExerciseForm({final Key? key}) : super(key: key);

  @override
  AbstractExerciseFormState createState() => _ExerciseCreateFormState();
}

class _ExerciseCreateFormState extends AbstractExerciseFormState with StringLocalizer {
  @override
  Future<ServerResponse<Exercise, String>> sendRequest(final ExerciseProvider provider, final ExerciseFormInput formInput) async {
    final exerciseCreate = _getExerciseCreate(formInput);
    return provider.postUserExercise(exerciseCreate);
  }

  @override
  void navigateAfterFormSubmit(final BuildContext context, final Exercise exercise) {
    Navigator.of(context).pushReplacementNamed(ExerciseDetailScreen.routeName, arguments: exercise);
  }

  static ExerciseCreate _getExerciseCreate(final ExerciseFormInput formInput) {
    return ExerciseCreate(
      name: formInput.name,
      description: formInput.descriptionNullOrNotBlank,
      muscleGroups: formInput.muscleGroupList,
      loggingTypes: formInput.loggingTypeList,
    );
  }
}
