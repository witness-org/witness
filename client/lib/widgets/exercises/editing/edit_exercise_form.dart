import 'package:client/models/exercises/exercise.dart';
import 'package:client/providers/exercise_provider.dart';
import 'package:client/services/server_response.dart';
import 'package:client/widgets/common/string_localizer.dart';
import 'package:client/widgets/exercises/details/exercise_detail_screen.dart';
import 'package:flutter/material.dart';

import 'package:client/widgets/exercises/editing/abstract_exercise_form.dart';

import 'package:client/widgets/exercises/editing/exercise_form_input.dart';

import 'package:client/widgets/exercises/exercises_screen.dart';

class EditExerciseForm extends AbstractExerciseForm {
  const EditExerciseForm(final Exercise exercise, {final Key? key}) : super(key: key, exercise: exercise);

  @override
  AbstractExerciseFormState createState() => _ExerciseEditFormState();
}

class _ExerciseEditFormState extends AbstractExerciseFormState with StringLocalizer {
  @override
  Future<ServerResponse<Exercise, String>> sendRequest(final ExerciseProvider provider, final ExerciseFormInput formInput) async {
    final exercise = _getExercise(formInput);

    if (exercise == null) {
      return const ServerResponse.failure("An unexpected error occurred. Please try again later.");
    }

    return provider.putUserExercise(exercise);
  }

  @override
  void navigateAfterFormSubmit(final BuildContext context, final Exercise exercise) {
    Navigator.of(context).pushNamedAndRemoveUntil(
      ExerciseDetailScreen.routeName,
      ModalRoute.withName(ExercisesScreen.routeName),
      arguments: exercise,
    );
  }

  Exercise? _getExercise(final ExerciseFormInput formInput) {
    return formInput.id == null
        ? null
        : Exercise(
            id: formInput.id!,
            name: formInput.name,
            description: formInput.descriptionNullOrNotEmpty,
            muscleGroups: formInput.muscleGroupList,
            loggingTypes: formInput.loggingTypeList,
          );
  }
}
