import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/exercise_tag.dart';
import 'package:client/services/mock_data/exercises/exercise_tags.dart' as mock_tags;
import 'package:client/services/mock_data/exercises/exercises.dart' as mock_exercises;

final _logger = getLogger('exercise_service');

class ExerciseService {
  Future<List<ExerciseTag>> getExerciseTags() async {
    _logger.i('GET https://api.my-service.at/exercise-tags');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_tags.data];
  }

  Future<List<Exercise>> getExercisesByTag(final int tagId) async {
    _logger.i('GET https://api.my-service.at/exercises/byTag?id=$tagId');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_exercises.byTagId(tagId)];
  }
}
