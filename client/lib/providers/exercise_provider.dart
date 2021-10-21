import 'package:client/extensions/enum_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/server_response.dart';
import 'package:client/services/exercise_service.dart';
import 'package:client/models/exercises/exercise_create.dart';
import 'package:collection/collection.dart' as collection;
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_provider');

class ExerciseProvider with ChangeNotifier {
  ExerciseProvider(this._auth, this._muscleGroups, this._exercises);

  ExerciseProvider.empty()
      : this(
          null,
          <MuscleGroup>[],
          <MuscleGroup, List<Exercise>>{},
        );

  ExerciseProvider.fromProviders(final AuthProvider auth, final ExerciseProvider? instance)
      : this(
          auth,
          instance?._muscleGroups ?? <MuscleGroup>[],
          instance?._exercises ?? <MuscleGroup, List<Exercise>>{},
        );

  final _exerciseService = ExerciseService();
  List<MuscleGroup> _muscleGroups;
  Map<MuscleGroup, List<Exercise>> _exercises;
  final AuthProvider? _auth;

  List<MuscleGroup> get muscleGroups {
    return collection.UnmodifiableListView(_muscleGroups);
  }

  Map<MuscleGroup, List<Exercise>> get exercises {
    return collection.UnmodifiableMapView(_exercises);
  }

  List<Exercise> getExercisesByMuscleGroup(final MuscleGroup group) {
    return collection.UnmodifiableListView(_exercises[group] ?? <Exercise>[]);
  }

  Future<void> fetchExercisesByMuscleGroup(final MuscleGroup group) async {
    _logger.i('Fetching exercises that train muscle group "${group.toUiString()}"');

    final response = await _exerciseService.getExercisesByMuscleGroup(group, await _auth?.getToken());
    final resultList = response.success;

    if (response.isSuccessAndResponse) {
      _exercises[group] = resultList!;
      notifyListeners();
    }

    _logger.i(resultList != null ? 'Received ${resultList.length} exercises' : 'Error while fetching exercises: ${response.error}');
  }

  Future<ServerResponse<Exercise, String>> postUserExercise(final ExerciseCreate data) async {
    return _exerciseService.postUserExercise(data, await _auth?.getToken());
  }
}
