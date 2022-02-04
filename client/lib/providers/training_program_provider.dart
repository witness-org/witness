import 'package:client/extensions/list_extensions.dart';
import 'package:client/extensions/map_extensions.dart';
import 'package:client/logging/logger_factory.dart';
import 'package:client/models/training_programs/overview/training_day_overview.dart';
import 'package:client/models/training_programs/overview/training_program_overview.dart';
import 'package:client/models/training_programs/overview/training_week_overview.dart';
import 'package:client/models/training_programs/workout.dart';
import 'package:client/providers/auth_provider.dart';
import 'package:client/services/training_program_service.dart';
import 'package:collection/collection.dart' as collection;
import 'package:flutter/material.dart';
import 'package:injector/injector.dart';

final _logger = getLogger('training_program_provider');

class TrainingProgramProvider with ChangeNotifier {
  TrainingProgramProvider._(this._auth, this._trainingPrograms, this._trainingWeeks, this._trainingDays, this._workouts);

  TrainingProgramProvider.empty()
      : this._(
          null,
          <TrainingProgramOverview>[],
          <int, List<TrainingWeekOverview>>{},
          <int, List<TrainingDayOverview>>{},
          <int, List<Workout>>{},
        );

  TrainingProgramProvider.fromProviders(final AuthProvider auth, final TrainingProgramProvider? instance)
      : this._(
          auth,
          (instance?._trainingPrograms).orEmpty(),
          (instance?._trainingWeeks).orEmpty(),
          (instance?._trainingDays).orEmpty(),
          (instance?._workouts).orEmpty(),
        );

  static final Injector _injector = Injector.appInstance;
  late final TrainingProgramService _trainingProgramService = _injector.get<TrainingProgramService>();

  List<TrainingProgramOverview> _trainingPrograms;

  /* TODO instead of the Map-approach, one could opt for creating separate Providers (TrainingWeekProvider, TrainingDayProvider, WorkoutProvider)
   This heavily depends on the underlying Domain Model, e.g. if TrainingDays and TrainingWeeks have their own primary key IDs.
   I did it like this for the sake of creating mockups more quickly. However, there would be two main benefits in creating separate providers:
   Firstly, accessing the data in the widgets is a bit nicer (no need for maps and map lookups, can directly define getters). Secondly, the scope of
   the providers is narrowed down, which is important for change notifications: In the current form, when calling notifyListeners(), all
   training-program-related widgets may be rebuilt instead of only the affected one (i.e. TrainingWeekDetailScreen).*/
  Map<int, List<TrainingWeekOverview>> _trainingWeeks;
  Map<int, List<TrainingDayOverview>> _trainingDays;
  Map<int, List<Workout>> _workouts;
  final AuthProvider? _auth; // ignore: unused_field

  List<TrainingProgramOverview> get trainingPrograms {
    return collection.UnmodifiableListView(_trainingPrograms);
  }

  List<TrainingWeekOverview> trainingWeeksOfProgram(final int programId) {
    return collection.UnmodifiableListView(_trainingWeeks[programId] ?? []);
  }

  List<TrainingDayOverview> trainingDaysOfWeek(final int weekId) {
    return collection.UnmodifiableListView(_trainingDays[weekId] ?? []);
  }

  List<Workout> workoutsOfDay(final int dayId) {
    return collection.UnmodifiableListView(_workouts[dayId] ?? []);
  }

  Future<void> fetchTrainingPrograms() async {
    _logger.i('Fetching training programs');

    _trainingPrograms = await _trainingProgramService.getTrainingProgramsOverview();
    notifyListeners();

    _logger.i('Received ${_trainingPrograms.length} training programs');
  }

  Future<void> deleteTrainingProgram(final int programId) async {
    _logger.i('Deleting training program with ID "$programId"');

    final programToDeleteIndex = _trainingPrograms.indexWhere((final program) => program.id == programId);

    if (programToDeleteIndex == -1) {
      _logger.d('There is no program with ID "$programId", aborting delete');
      return;
    }

    await _trainingProgramService.deleteTrainingProgram(programId);
    _trainingPrograms.removeAt(programToDeleteIndex);
    notifyListeners();

    _logger.i('Successfully deleted training program with ID" $programId"');
  }

  Future<void> fetchTrainingWeeks(final int programId) async {
    _logger.i('Fetching training weeks of program with ID "$programId"');

    final fetchedWeeks = await _trainingProgramService.getWeeksOverviewOfProgram(programId);
    _trainingWeeks[programId] = fetchedWeeks;
    notifyListeners();

    _logger.i('Received ${fetchedWeeks.length} training weeks');
  }

  Future<void> fetchTrainingDays(final int weekId) async {
    _logger.i('Fetching training days of week with ID "$weekId"');

    final fetchedDays = await _trainingProgramService.getDaysOfWeek(weekId);
    _trainingDays[weekId] = fetchedDays;
    notifyListeners();

    _logger.i('Received ${fetchedDays.length} training days');
  }

  Future<void> fetchWorkouts(final int dayId) async {
    _logger.i('Fetching workouts of day with ID "$dayId"');

    final fetchedWorkouts = await _trainingProgramService.getWorkoutsOfDay(dayId);
    _workouts[dayId] = fetchedWorkouts;
    notifyListeners();

    _logger.i('Received ${fetchedWorkouts.length} workouts');
  }
}
