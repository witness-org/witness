import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/models/training_programs/workout.dart';
import 'package:client/services/mock_data/training_programs/training_programs.dart' as mock_training_programs;

final _logger = getLogger('training_program_service');

class TrainingProgramService {
  Future<List<TrainingProgramOverview>> getTrainingProgramsOverview() async {
    _logger.i('GET https://api.my-service.at/training-programs-overview');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_training_programs.getOverview()];
  }

  Future<void> deleteTrainingProgram(final int trainingProgramId) async {
    _logger.i('DELETE https://api.my-service.at/training-programs/$trainingProgramId');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return mock_training_programs.deleteProgram(trainingProgramId);
  }

  Future<List<TrainingWeekOverview>> getWeeksOverviewOfProgram(final int programId) async {
    _logger.i('GET https://api.my-service.at/training-programs/$programId/weeks-overview');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_training_programs.getWeeksOverview(programId)];
  }

  Future<List<TrainingDayOverview>> getDaysOfWeek(final int weekId) async {
    _logger.i('GET https://api.my-service.at/training-weeks/$weekId/days-overview');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_training_programs.getDays(weekId)];
  }

  Future<List<Workout>> getWorkoutsOfDay(final int dayId) async {
    _logger.i('GET https://api.my-service.at/training-days/$dayId/workouts');

    await Future<void>.delayed(
      const Duration(seconds: 1),
    );

    return [...mock_training_programs.getWorkouts(dayId)];
  }
}
