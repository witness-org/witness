import 'package:client/extensions/enum_extensions.dart';
import 'package:client/extensions/list_extensions.dart';
import 'package:client/extensions/map_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_create.dart';
import 'package:client/models/exercises/exercise_history.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/exercise_service.dart';
import 'package:client/services/server_response.dart';
import 'package:collection/collection.dart' as collection;
import 'package:flutter/material.dart';
import 'package:injector/injector.dart';

final _logger = getLogger('exercise_provider');

class ExerciseProvider with ChangeNotifier {
  ExerciseProvider._(this._auth, this._exercises, this._exerciseHistories);

  ExerciseProvider.empty() : this._(null, <MuscleGroup, List<Exercise>>{}, <int, ExerciseHistory>{});

  ExerciseProvider.fromProviders(final AuthProvider auth, final ExerciseProvider? instance)
      : this._(auth, (instance?._exercises).orEmpty(), (instance?._exerciseHistories).orEmpty());

  static final Injector _injector = Injector.appInstance;
  late final ExerciseService _exerciseService = _injector.get<ExerciseService>();

  final Map<MuscleGroup, List<Exercise>> _exercises;
  final Map<int, ExerciseHistory> _exerciseHistories;
  final AuthProvider? _auth;

  Map<MuscleGroup, List<Exercise>> get exercises {
    return collection.UnmodifiableMapView(_exercises);
  }

  ExerciseHistory? getExercisesHistory(final int exerciseId) {
    return _exerciseHistories[exerciseId];
  }

  List<Exercise> getExercisesByMuscleGroup(final MuscleGroup group) {
    return collection.UnmodifiableListView(_exercises[group].orEmpty());
  }

  Future<void> fetchExercisesByMuscleGroup(final MuscleGroup group) async {
    _logger.i('Fetching exercises that train muscle group "${group.toUiString()}"');

    final response = await _exerciseService.getExercisesByMuscleGroup(group, await _auth?.getToken());
    final resultList = response.success;

    if (response.isSuccessAndResponse) {
      _exercises[group] = resultList!;
      notifyListeners();
    }

    if (resultList != null) {
      _logger.i('Received ${resultList.length} exercises');
    } else {
      _logger.e('Fetching exercises failed');
      return Future.error(response.error ?? '');
    }
  }

  Future<ServerResponse<Exercise, String?>> postUserExercise(final ExerciseCreate newExercise) async {
    _logger.i('Creating new user exercise');

    final response = await _exerciseService.postUserExercise(newExercise, await _auth?.getToken());
    if (response.isSuccessAndResponse) {
      _logger.i('Creation of exercise succeeded');

      final createdExercise = response.success!;
      for (final muscleGroup in createdExercise.muscleGroups) {
        _exercises[muscleGroup] ??= [];
        _exercises[muscleGroup]!.add(createdExercise);
      }

      notifyListeners();
    } else {
      _logger.e('Creation of exercise failed: ${response.error}');
    }
    return response;
  }

  Future<ServerResponse<Exercise, String?>> putUserExercise(final Exercise data) async {
    _logger.i('Updating user exercise with ID "${data.id}"');

    final response = await _exerciseService.putUserExercise(data, await _auth?.getToken());
    if (response.isSuccessAndResponse) {
      _logger.i('Update of exercise succeeded');

      final updatedExercise = response.success!;
      for (final muscleGroup in MuscleGroup.values) {
        final indexInMuscleGroupList = _exercises[muscleGroup]?.indexWhere((final exercise) => exercise.id == updatedExercise.id);
        final hasMuscleGroup = updatedExercise.muscleGroups.contains(muscleGroup);

        if (hasMuscleGroup) {
          // updated exercise is part of muscle group

          if (indexInMuscleGroupList == null) {
            // exercise was added to muscle group as first exercise, create list
            _exercises[muscleGroup] = [updatedExercise];
          } else if (indexInMuscleGroupList == -1) {
            // muscle group already contains exercises, updated one is now also part of it, append it
            _exercises[muscleGroup]!.add(updatedExercise);
          } else {
            // exercise was already part of muscle group, it only needs to be replaced
            _exercises[muscleGroup]![indexInMuscleGroupList] = updatedExercise;
          }
        } else {
          // updated exercise is not part of muscle group

          if (indexInMuscleGroupList == null) {
            // muscle group list was empty, nothing to do
          } else if (indexInMuscleGroupList == -1) {
            // muscle group did not contain exercise, nothing to do
          } else {
            // exercise was part of muscle group, but is not anymore, remove it
            _exercises[muscleGroup]!.removeAt(indexInMuscleGroupList);
          }
        }
      }
      notifyListeners();
    } else {
      _logger.e('Update of exercise failed: ${response.error}');
    }
    return response;
  }

  Future<ServerResponse<void, String?>> deleteUserExercise(final int exerciseId) async {
    _logger.i('Deleting user exercise with ID "$exerciseId"');

    final response = await _exerciseService.deleteUserExercise(exerciseId, await _auth?.getToken());
    if (response.isSuccessNoResponse) {
      _logger.i('Deletion of user exercise succeeded');
      for (final muscleGroup in MuscleGroup.values) {
        _exercises[muscleGroup]?.removeWhere((final exercise) => exercise.id == exerciseId);
      }
      notifyListeners();
    } else if (response.isError) {
      _logger.e('Deletion of user exercise failed: ${response.error}');
    }
    return response;
  }

  Future<void> fetchExerciseHistory(final int exerciseId) async {
    _logger.i('Fetching exercise history for exercise with ID $exerciseId');

    final response = await _exerciseService.getExerciseHistory(exerciseId, await _auth?.getToken());
    final exerciseHistory = response.success;

    if (response.isSuccessAndResponse) {
      if (exerciseHistory!.entries.isNotEmpty) {
        _exerciseHistories[exerciseId] = exerciseHistory;
      }
      notifyListeners();
    }

    if (exerciseHistory != null) {
      _logger.i('Received ${exerciseHistory.entries.length} exercise history entries');
    } else {
      _logger.e('Fetching exercise history failed');
      return Future.error(response.error ?? '');
    }
  }
}
