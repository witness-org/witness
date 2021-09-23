import 'package:client/logging/logger_factory.dart';
import 'package:client/models/exercises/exercise.dart';
import 'package:client/models/exercises/muscle_group.dart';
import 'package:client/services/mock_data/exercises/exercises.dart' as mock_exercises;
import 'package:client/services/mock_data/exercises/muscle_groups.dart' as mock_muscle_groups;

final _logger = getLogger('exercise_service');

class ExerciseService {
  Future<List<MuscleGroup>> getMuscleGroups() async {
    _logger.i('GET https://api.my-service.at/exercises/muscle-groups');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_muscle_groups.data];
  }

  Future<List<Exercise>> getExercisesByMuscleGroup(final int groupId) async {
    _logger.i('GET https://api.my-service.at/exercises/byMuscleGroup?id=$groupId');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_exercises.byGroupId(groupId)];
  }
}
