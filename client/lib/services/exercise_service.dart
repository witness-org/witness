import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercise.dart';
import 'package:client/models/exercise_tag.dart';
import 'package:client/services/mock_data/exercise_tags.dart' as mock_tags;
import 'package:client/services/mock_data/exercises.dart' as mock_exercises;

final _logger = getLogger('exercise_service');

class ExerciseService {
  Future<List<ExerciseTag>> getExerciseTags() async {
    _logger.d('GET https://api.my-service.at/exercise-tags');

    await Future.delayed(
      const Duration(seconds: 1),
    );

    final fetchedTags = [...mock_tags.data];

    return fetchedTags;
  }

  Future<List<Exercise>> getExercisesByTag(int tagId) async {
    _logger.d('GET https://api.my-service.at/exercises/byTag?id=$tagId');

    await Future.delayed(
      const Duration(seconds: 1),
    );

    final fetchedExercises = [...mock_exercises.byTagId(tagId)];

    return fetchedExercises;
  }
}
