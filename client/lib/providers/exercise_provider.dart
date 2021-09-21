import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_tag.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/exercise_service.dart';
import 'package:collection/collection.dart' as collection;
import 'package:flutter/material.dart';

final _logger = getLogger('exercise_provider');

class ExerciseProvider with ChangeNotifier {
  ExerciseProvider(this._userId, this._authToken, this._isAuthenticated, this._exerciseTags, this._exercises);

  ExerciseProvider.empty()
      : this(
          null,
          null,
          false,
          <ExerciseTag>[],
          <ExerciseTag, List<Exercise>>{},
        );

  ExerciseProvider.fromProviders(final AuthProvider auth, final ExerciseProvider? instance)
      : this(
          auth.userId,
          auth.token,
          auth.isAuthenticated,
          instance?._exerciseTags ?? <ExerciseTag>[],
          instance?._exercises ?? <ExerciseTag, List<Exercise>>{},
        );

  final _exerciseService = ExerciseService();
  List<ExerciseTag> _exerciseTags;
  Map<ExerciseTag, List<Exercise>> _exercises;
  final String? _userId; // ignore: unused_field
  final String? _authToken; // ignore: unused_field
  final bool _isAuthenticated; // ignore: unused_field

  List<ExerciseTag> get exerciseTags {
    return collection.UnmodifiableListView(_exerciseTags);
  }

  Map<ExerciseTag, List<Exercise>> get exercises {
    return collection.UnmodifiableMapView(_exercises);
  }

  List<Exercise> getExercisesByTags(final ExerciseTag tag) {
    return collection.UnmodifiableListView(_exercises[tag] ?? <Exercise>[]);
  }

  Future<void> fetchTags() async {
    _logger.i('Fetching exercise tags');

    _exerciseTags = await _exerciseService.getExerciseTags();
    notifyListeners();

    _logger.i('Received ${_exerciseTags.length} exercise tags');
  }

  Future<void> fetchExercisesByTag(final ExerciseTag tag) async {
    _logger.i('Fetching exercises that contain tag "${tag.name}" (id "${tag.id}")');

    final fetchedExercises = await _exerciseService.getExercisesByTag(tag.id);
    notifyListeners();

    _exercises[tag] = fetchedExercises;

    _logger.i('Received ${fetchedExercises.length} exercises');
  }
}