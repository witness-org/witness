import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/exercise_service.dart';
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
  final AuthProvider? _auth; // ignore: unused_field

  List<MuscleGroup> get muscleGroups {
    return collection.UnmodifiableListView(_muscleGroups);
  }

  Map<MuscleGroup, List<Exercise>> get exercises {
    return collection.UnmodifiableMapView(_exercises);
  }

  List<Exercise> getExercisesByMuscleGroup(final MuscleGroup group) {
    return collection.UnmodifiableListView(_exercises[group] ?? <Exercise>[]);
  }

  Future<void> fetchMuscleGroups() async {
    _logger.i('Fetching muscle groups');

    _muscleGroups = await _exerciseService.getMuscleGroups();
    notifyListeners();

    _logger.i('Received ${_muscleGroups.length} muscle groups');
  }

  Future<void> fetchExercisesByMuscleGroup(final MuscleGroup group) async {
    _logger.i('Fetching exercises that affect muscle group "${group.name}" (id "${group.id}")');

    final fetchedExercises = await _exerciseService.getExercisesByMuscleGroup(group.id);
    notifyListeners();

    _exercises[group] = fetchedExercises;

    _logger.i('Received ${fetchedExercises.length} exercises');
  }
}
